"""Register a second car media browser and scoped audio permissions in the supplied binary manifest."""
import struct
def media_manifest(xml):
    raw=bytearray(xml);pool=8
    _,header,old_size,count,styles,flags,start,style_start=struct.unpack_from('<HHIIIIII',raw,pool)
    assert styles==0 and style_start==0 and not flags&256
    offsets=list(struct.unpack_from('<'+'I'*count,raw,pool+header))
    data=bytearray(raw[pool+start:pool+old_size])
    def string(i):
        length=struct.unpack_from('<H',data,offsets[i])[0]
        return data[offsets[i]+2:offsets[i]+2+length*2].decode('utf-16-le')
    names=[string(i) for i in range(count)]
    new=['io.memorydrive.pleos.DriveAudioService','android.permission.READ_MEDIA_AUDIO','android.permission.READ_EXTERNAL_STORAGE','android.permission.BLUETOOTH_CONNECT','android.permission.BLUETOOTH','android.permission.BLUETOOTH_SCAN','android.permission.BLUETOOTH_ADVERTISE']
    new.append('android.intent.action.SEND')
    share_index=count+len(new)-1
    for name in new:
        offsets.append(len(data));data.extend(struct.pack('<H',len(name))+name.encode('utf-16-le')+b'\0\0')
    data.extend(b'\0'*(-len(data)%4))
    table=struct.pack('<'+'I'*len(offsets),*offsets)
    newstart=header+len(table);size=newstart+len(data)
    rebuilt=bytearray(raw[pool:pool+header]);struct.pack_into('<II',rebuilt,4,size,len(offsets));struct.pack_into('<I',rebuilt,20,newstart)
    out=bytearray(raw[:pool])+rebuilt+table+data
    cursor=pool+old_size;collect=None;depth=0;added=0
    query_start=None;share_action=None;share_mime=None;share_query_count=0;updated_version=False
    while cursor<len(raw):
        typ,_,length=struct.unpack_from('<HHI',raw,cursor);chunk=bytearray(raw[cursor:cursor+length]);cursor+=length;out.extend(chunk)
        if typ==0x102 and collect is None:
            tag=names[struct.unpack_from('<I',chunk,20)[0]]
            if tag=='intent':query_start=len(out)-length;share_action=None;share_mime=None
            _,attr_size,num=struct.unpack_from('<HHH',chunk,24);items=[]
            for i in range(num):
                off=16+struct.unpack_from('<H',chunk,24)[0]+i*attr_size
                name,raw_idx=struct.unpack_from('<II',chunk,off+4)
                if tag=='manifest' and names[name]=='versionCode':
                    assert struct.unpack_from('<I',chunk,off+16)[0]==50
                    struct.pack_into('<I',out,len(out)-length+off+16,51)
                    updated_version=True
                if raw_idx<len(names):
                    value=names[raw_idx];attr=names[name]
                    if attr=='name':items.append((off,value))
                    if query_start is not None and tag=='action' and attr=='name' and value=='android.intent.action.OPEN_DOCUMENT':share_action=len(out)-length-query_start+off
                    if query_start is not None and tag=='data' and attr=='mimeType' and value=='*/*':share_mime=len(out)-length-query_start+off
            for off,val in items:
                targets=[count] if tag=='service' and val=='io.memorydrive.pleos.MemoryMusicService' else list(range(count+1,share_index)) if tag=='uses-permission' and val=='android.permission.FOREGROUND_SERVICE' else []
                if targets:collect=(bytearray(),off,targets);depth=0;break
        if typ==0x103 and query_start is not None and names[struct.unpack_from('<I',chunk,20)[0]]=='intent':
            if share_action is not None and share_mime is not None:
                query=bytearray(out[query_start:])
                for offset,index in [(share_action,share_index),(share_mime,names.index('application/json'))]:
                    struct.pack_into('<I',query,offset+8,index);struct.pack_into('<I',query,offset+16,index)
                out.extend(query);share_query_count+=1
            query_start=None
        if collect:
            copy,off,targets=collect;copy.extend(chunk);depth+=int(typ==0x102)-int(typ==0x103)
            if depth==0:
                for idx in targets:
                    item=bytearray(copy);struct.pack_into('<I',item,off+8,idx);struct.pack_into('<I',item,off+16,idx);out.extend(item);added+=1
                collect=None
    assert added==len(new)-1 and share_query_count==1 and updated_version
    struct.pack_into('<I',out,4,len(out));return out
