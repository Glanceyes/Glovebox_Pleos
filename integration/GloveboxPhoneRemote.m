#import <Foundation/Foundation.h>
#import <IOBluetooth/IOBluetooth.h>
#include <arpa/inet.h>
#include <signal.h>
#include <unistd.h>

/* A physical AVRCP controller for the Mac-hosted Pleos emulator. Standard
   GetPlayStatus / GetElementAttributes responses are the only state source. */
static uint32_t read32(const uint8_t *p) {
    uint32_t v; memcpy(&v,p,sizeof(v)); return ntohl(v);
}
static uint16_t read16(const uint8_t *p) {
    uint16_t v; memcpy(&v,p,sizeof(v)); return ntohs(v);
}
static void publish(NSDictionary *event) {
    NSData *d=[NSJSONSerialization dataWithJSONObject:event options:0 error:nil];
    if(d){fwrite(d.bytes,1,d.length,stdout);fputc('\n',stdout);fflush(stdout);}
}
@interface PhoneRemote:NSObject <IOBluetoothL2CAPChannelDelegate>
@property(strong) IOBluetoothDevice *phone;
@property(strong) IOBluetoothL2CAPChannel *channel;
@property(copy) NSString *title;
@property(copy) NSString *artist;
@property NSUInteger label,ticks;
@property BOOL running, ready;
@property NSTimeInterval lastStatus,lastMetadata;
@end
@implementation PhoneRemote
-(void)finish {
    self.running=NO;self.ready=NO;
}
-(void)connect {
    self.running=YES;self.title=@"";self.artist=@"";
    IOReturn rc=[self.phone openConnection:self withPageTimeout:0 authenticationRequired:YES];
    if(rc==kBluetoothHCIErrorACLConnectionAlreadyExists && self.phone.isConnected){
        if([self.phone requestAuthentication]==kIOReturnSuccess)[self connectionComplete:self.phone status:0];else [self finish];
    }else if(rc!=0)[self finish];
}
-(void)connectionComplete:(IOBluetoothDevice*)peer status:(IOReturn)status {
    if(status!=0){
        if(status!=kBluetoothHCIErrorACLConnectionAlreadyExists || [peer requestAuthentication]!=kIOReturnSuccess){[self finish];return;}
    }
    IOBluetoothSDPUUID *uuid=[IOBluetoothSDPUUID uuid16:0x110c];
    if([peer getServiceRecordForUUID:uuid]) [self sdpQueryComplete:peer status:0];
    else if([peer performSDPQuery:self uuids:@[uuid]]!=0)[self finish];
}
-(void)sdpQueryComplete:(IOBluetoothDevice*)peer status:(IOReturn)status {
    IOBluetoothSDPServiceRecord *record=[peer getServiceRecordForUUID:[IOBluetoothSDPUUID uuid16:0x110c]];
    BluetoothL2CAPPSM psm=0;
    if(status!=0||record==nil||[record getL2CAPPSM:&psm]!=0||psm==0){[self finish];return;}
    IOBluetoothL2CAPChannel *channel=nil;
    if([peer openL2CAPChannelAsync:&channel withPSM:psm delegate:self]!=0){[self finish];return;}
    self.channel=channel;
}
-(void)write:(NSMutableData*)data {
    if(self.ready&&[self.channel writeSync:data.mutableBytes length:(uint16_t)data.length]!=0)[self finish];
}
-(void)sendPDU:(uint8_t)pdu bytes:(const uint8_t*)payload count:(uint16_t)count {
    uint8_t header[]={(uint8_t)((self.label++ & 15)<<4),0x11,0x0e,1,0x48,0,0,0x19,0x58,pdu,0,(uint8_t)(count>>8),(uint8_t)count};
    NSMutableData *data=[NSMutableData dataWithBytes:header length:sizeof(header)];
    if(count)[data appendBytes:payload length:count];
    [self write:data];
}
-(void)metadata {
    const uint8_t fields[]={0,0,0,0,0,0,0,0,2,0,0,0,1,0,0,0,2};
    [self sendPDU:0x20 bytes:fields count:sizeof(fields)];
}
-(void)tick {
    if(!self.ready)return;
    if((self.ticks++%5)==0)[self metadata];else [self sendPDU:0x30 bytes:NULL count:0];
    if(NSProcessInfo.processInfo.systemUptime-self.lastStatus>12)[self finish];
}
-(void)l2capChannelOpenComplete:(IOBluetoothL2CAPChannel*)channel status:(IOReturn)status {
    if(status!=0){[self finish];return;}
    self.ready=YES;self.lastStatus=NSProcessInfo.processInfo.systemUptime;[self metadata];
}
-(void)l2capChannelClosed:(IOBluetoothL2CAPChannel*)channel {[self finish];}
-(void)button:(uint8_t)key released:(BOOL)released {
    uint8_t frame[]={(uint8_t)((self.label++ & 15)<<4),0x11,0x0e,0,0x48,0x7c,(uint8_t)(key|(released?0x80:0)),0};
    [self write:[NSMutableData dataWithBytes:frame length:sizeof(frame)]];
}
-(void)command:(NSString*)text {
    if(!self.ready)return;
    uint8_t key;
    if([text isEqualToString:@"play"])key=0x44;
    else if([text isEqualToString:@"pause"])key=0x46;
    else return;
    [self button:key released:NO];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW,80*NSEC_PER_MSEC),dispatch_get_main_queue(),^{
        [self button:key released:YES];
        [self sendPDU:0x30 bytes:NULL count:0];
    });
}
-(void)attributes:(const uint8_t*)payload length:(NSUInteger)n {
    if(n<1)return;
    NSUInteger offset=1;
    NSString *title=@"",*artist=@"";
    for(NSUInteger i=0;i<payload[0];i++){
        if(offset+8>n)return;
        uint32_t field=read32(payload+offset);
        uint16_t charset=read16(payload+offset+4),len=read16(payload+offset+6);
        offset+=8;if(offset+len>n)return;
        NSStringEncoding enc=charset==1013?NSUTF16BigEndianStringEncoding:charset==1014?NSUTF16LittleEndianStringEncoding:charset==1015?NSUTF16StringEncoding:NSUTF8StringEncoding;
        NSString *value=[[NSString alloc] initWithBytes:payload+offset length:len encoding:enc];
        if(value.length>160)value=[value substringToIndex:160];
        if(field==1 && value)title=value;
        if(field==2 && value)artist=value;
        offset+=len;
    }
    self.title=title;self.artist=artist;self.lastMetadata=NSProcessInfo.processInfo.systemUptime;
}
-(void)l2capChannelData:(IOBluetoothL2CAPChannel*)channel data:(void*)bytes length:(size_t)n {
    const uint8_t *b=bytes;
    if(n<6||(b[0]&0x0d)!=0||b[1]!=0x11||b[2]!=0x0e)return;
    if(!(b[0]&2)){ // This endpoint implements the controller role only.
        NSMutableData *reply=[NSMutableData dataWithBytes:b length:n];
        uint8_t *r=reply.mutableBytes;r[0]|=2;r[3]=8;[self write:reply];return;
    }
    if(n<13||b[5]!=0||b[3]!=0x0c||b[6]!=0||b[7]!=0x19||b[8]!=0x58||b[10]!=0)return;
    NSUInteger total=read16(b+11);
    if(total>n-13)return;
    if(b[9]==0x20){[self attributes:b+13 length:total];return;}
    if(b[9]!=0x30||total!=9)return;
    self.lastStatus=NSProcessInfo.processInfo.systemUptime;
    uint32_t duration=read32(b+13),position=read32(b+17);
    uint8_t state=b[21];
    if(state!=1 && state!=2){publish(@{@"media":@{}});return;}
    BOOL fresh=self.lastStatus-self.lastMetadata<7;
    publish(@{@"media":@{@"playing":@(state==1),@"position":position==UINT32_MAX?@(-1):@(position),
        @"duration":duration==UINT32_MAX?@(-1):@(duration),@"title":fresh?self.title:@"",@"artist":fresh?self.artist:@""}});
}
@end

int main(){@autoreleasepool{
    signal(SIGPIPE,SIG_IGN);
    char address[32];if(!fgets(address,sizeof(address),stdin))return 2;
    NSString *value=[[NSString stringWithUTF8String:address] stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
    if([value rangeOfString:@"^([[:xdigit:]]{2}:){5}[[:xdigit:]]{2}$" options:NSRegularExpressionSearch].location==NSNotFound)return 2;
    PhoneRemote *remote=[PhoneRemote new];remote.phone=[IOBluetoothDevice deviceWithAddressString:value];
    if(!remote.phone)return 2;
    [remote connect];
    dispatch_async(dispatch_get_global_queue(QOS_CLASS_UTILITY,0),^{
        char command[32];while(fgets(command,sizeof(command),stdin)){
            NSString *text=[[NSString stringWithUTF8String:command] stringByTrimmingCharactersInSet:NSCharacterSet.whitespaceAndNewlineCharacterSet];
            dispatch_async(dispatch_get_main_queue(),^{[remote command:text];});
        }
        dispatch_async(dispatch_get_main_queue(),^{[remote finish];});
    });
    NSDate *deadline=[NSDate dateWithTimeIntervalSinceNow:20];
    while(remote.running && (remote.ready || deadline.timeIntervalSinceNow>0)){
        [NSRunLoop.currentRunLoop runUntilDate:[NSDate dateWithTimeIntervalSinceNow:.55]];
        [remote tick];
    }
    if(remote.channel)[remote.channel closeChannel];
    return 0;
}}
