param([switch]$ReuseSmali)
$ErrorActionPreference = 'Stop'
$patchRoot = $PSScriptRoot
$javaBin = (Get-ChildItem -LiteralPath "$patchRoot\tools\jdk" -Recurse -Filter java.exe | Select-Object -First 1).DirectoryName
$buildTools = "$patchRoot\tools\build-tools\android-15"
$androidJar = "$patchRoot\tools\platform\android-35\android.jar"
New-Item -ItemType Directory -Force "$patchRoot\java-classes","$patchRoot\java-dex" | Out-Null
$javaSources = Get-ChildItem "$patchRoot\java" -Recurse -Filter '*.java' | ForEach-Object FullName
& "$javaBin\javac.exe" -encoding UTF-8 -source 8 -target 8 -classpath $androidJar -d "$patchRoot\java-classes" $javaSources
if ($LASTEXITCODE -ne 0) { throw 'Native design helper compilation failed' }
& "$javaBin\jar.exe" cf "$patchRoot\native-design.jar" -C "$patchRoot\java-classes" .
& "$javaBin\java.exe" -cp "$buildTools\lib\d8.jar" com.android.tools.r8.D8 --lib $androidJar --min-api 28 --output "$patchRoot\java-dex" "$patchRoot\native-design.jar"
if ($LASTEXITCODE -ne 0) { throw 'Native design DEX compilation failed' }
if (-not $ReuseSmali) {
    python "$patchRoot\apply-design.py"
    if ($LASTEXITCODE -ne 0) { throw 'Design patch failed' }
    python "$patchRoot\apply-features.py"
    if ($LASTEXITCODE -ne 0) { throw 'Feature presentation patch failed' }
    & "$javaBin\java.exe" -jar "$patchRoot\tools\apktool.jar" b "$patchRoot\patch" -o "$patchRoot\glovebox-unsigned.apk"
    if ($LASTEXITCODE -ne 0) { throw 'APK build failed' }
}
python "$patchRoot\repackage.py"
if ($LASTEXITCODE -ne 0) { throw 'Original resource preservation check failed' }
& "$buildTools\zipalign.exe" -P 16 -f 4 "$patchRoot\glovebox-preserved.apk" "$patchRoot\glovebox-aligned.apk"
if ($LASTEXITCODE -ne 0) { throw 'APK alignment failed' }
# This is a local test key, deliberately unrelated to the production signing key.
& "$javaBin\java.exe" -jar "$buildTools\lib\apksigner.jar" sign --ks "$patchRoot\glovebox-test.p12" --ks-key-alias glovebox-test --ks-pass pass:glovebox-local-test --out "$patchRoot\Glovebox-native-test.apk" "$patchRoot\glovebox-aligned.apk"
if ($LASTEXITCODE -ne 0) { throw 'APK signing failed' }
& "$javaBin\java.exe" -jar "$buildTools\lib\apksigner.jar" verify --verbose "$patchRoot\Glovebox-native-test.apk"
if ($LASTEXITCODE -ne 0) { throw 'APK verification failed' }
