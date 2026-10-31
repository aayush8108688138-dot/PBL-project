# PowerShell Runner for Student Analytics Project
$ErrorActionPreference = "Stop"

Write-Host "Resolving classpath..." -ForegroundColor Cyan
$m2Repo = Join-Path $HOME ".m2\repository"
$jars = (Get-ChildItem -Path $m2Repo -Recurse -Filter "*.jar" -ErrorAction SilentlyContinue).FullName -join ";"

Write-Host "Compiling latest source changes..." -ForegroundColor Cyan
$srcFiles = (Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java").FullName
javac -d "target\classes" -cp "target\classes;$jars" $srcFiles
Copy-Item -Path "src\main\resources\*" -Destination "target\classes\" -Recurse -Force -ErrorAction SilentlyContinue

$classpath = "target\classes;src\main\resources;$jars"

Write-Host "Launching Student Lifestyle & Academic Performance Analytics..." -ForegroundColor Green
java -cp $classpath com.analytics.student.ui.MainFrame

