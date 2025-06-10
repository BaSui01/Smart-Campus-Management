# =====================================================
# Smart Campus Management System - Simple Database Initialization Script
# File: Execute-Scripts-Simple.ps1
# Description: Simplified PowerShell script to execute SQL scripts
# Version: 1.0.0
# Created: 2025-01-27
# =====================================================

# Set console encoding
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# Get script directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path

# Database connection configuration
$dbHost = "localhost"
$dbPort = "3306"
$dbUser = "root"
$dbPassword = "xiaoxiao123"

# Script lists
$optimizedScripts = @(
    "01_create_complete_tables.sql",
    "02_insert_large_scale_data.sql", 
    "11_optimized_data_generation.sql",
    "12_data_validation_and_statistics.sql"
)

$originalScripts = @(
    "01_create_complete_tables.sql",
    "02_insert_large_scale_data.sql",
    "08_complete_data_generation_fixed.sql",
    "09_business_data_generation_fixed.sql", 
    "10_financial_and_other_data_fixed.sql",
    "07_data_validation_and_statistics.sql"
)

# Function to display colored messages
function Write-Message {
    param([string]$Message, [string]$Color = "White")
    Write-Host $Message -ForegroundColor $Color
}

# Function to display title
function Write-Title {
    param([string]$Title)
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Cyan
    Write-Host $Title -ForegroundColor Cyan
    Write-Host "=====================================================" -ForegroundColor Cyan
    Write-Host ""
}

# Function to test MySQL connection
function Test-MySQL {
    try {
        # Create a temporary file with test query
        $tempFile = [System.IO.Path]::GetTempFileName()
        "SELECT 1;" | Out-File -FilePath $tempFile -Encoding UTF8
        
        # Test connection
        $process = Start-Process -FilePath "mysql" -ArgumentList @(
            "-h$dbHost", "-P$dbPort", "-u$dbUser", "-p$dbPassword"
        ) -RedirectStandardInput $tempFile -RedirectStandardOutput $null -RedirectStandardError $null -Wait -PassThru -NoNewWindow
        
        Remove-Item $tempFile -Force
        return $process.ExitCode -eq 0
    }
    catch {
        return $false
    }
}

# Function to execute SQL file
function Invoke-SQLFile {
    param([string]$FilePath, [string]$FileName)
    
    Write-Message "Executing: $FileName" "Yellow"
    
    if (-not (Test-Path $FilePath)) {
        Write-Message "X File not found: $FilePath" "Red"
        return $false
    }
    
    try {
        $startTime = Get-Date
        
        # Execute using Start-Process with redirection
        $process = Start-Process -FilePath "mysql" -ArgumentList @(
            "-h$dbHost", "-P$dbPort", "-u$dbUser", "-p$dbPassword"
        ) -RedirectStandardInput $FilePath -RedirectStandardOutput $null -RedirectStandardError $null -Wait -PassThru -NoNewWindow
        
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds
        
        if ($process.ExitCode -eq 0) {
            Write-Message "√ $FileName executed successfully (Duration: $([math]::Round($duration, 2))s)" "Green"
            return $true
        } else {
            Write-Message "X $FileName execution failed (Exit code: $($process.ExitCode))" "Red"
            return $false
        }
    }
    catch {
        Write-Message "X $FileName execution exception: $($_.Exception.Message)" "Red"
        return $false
    }
}

# Main program
Write-Title "Smart Campus Management System - Database Initialization"

# Test MySQL connection
Write-Message "Testing MySQL connection..." "Yellow"
if (-not (Test-MySQL)) {
    Write-Message "X Cannot connect to MySQL database" "Red"
    Write-Message "Please check:" "Red"
    Write-Message "  1. MySQL service is running" "Red"
    Write-Message "  2. Connection parameters: Host=$dbHost, Port=$dbPort, User=$dbUser" "Red"
    Write-Message "  3. Password is correct" "Red"
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Message "√ MySQL connection successful" "Green"

# Select execution plan
Write-Host ""
Write-Message "Please select execution plan:" "Cyan"
Write-Message "1. Optimized version (Recommended) - Fast batch data generation" "White"
Write-Message "2. Original version (Backup) - Step-by-step data generation" "White"
Write-Message "3. Exit" "White"
Write-Host ""

do {
    $choice = Read-Host "Enter your choice (1-3)"
    switch ($choice) {
        "1" { 
            $scripts = $optimizedScripts
            $versionName = "Optimized Version"
            break 
        }
        "2" { 
            $scripts = $originalScripts
            $versionName = "Original Version"
            break 
        }
        "3" { 
            Write-Message "Exit execution" "Yellow"
            exit 0 
        }
        default { 
            Write-Message "Invalid choice, please enter 1, 2 or 3" "Red"
        }
    }
} while ($choice -notin @("1", "2", "3"))

Write-Title "Starting execution of $versionName scripts"

# Display files to execute
Write-Message "About to execute the following script files:" "Yellow"
foreach ($script in $scripts) {
    Write-Message "  • $script" "White"
}
Write-Host ""

$confirm = Read-Host "Confirm execution? (y/N)"
if ($confirm -ne "y" -and $confirm -ne "Y") {
    Write-Message "Execution cancelled" "Yellow"
    exit 0
}

# Execute scripts
$startTime = Get-Date
$successCount = 0
$failCount = 0

Write-Title "Executing SQL Scripts"

foreach ($script in $scripts) {
    $filePath = Join-Path $scriptPath $script
    
    if (Invoke-SQLFile -FilePath $filePath -FileName $script) {
        $successCount++
    } else {
        $failCount++
        
        Write-Host ""
        $continue = Read-Host "Script execution failed, continue with remaining scripts? (y/N)"
        if ($continue -ne "y" -and $continue -ne "Y") {
            Write-Message "User chose to stop execution" "Yellow"
            break
        }
    }
    Write-Host ""
}

$endTime = Get-Date
$totalDuration = ($endTime - $startTime).TotalSeconds

# Display results
Write-Title "Execution Statistics Report"
Write-Host "Total Files: $($scripts.Count)" -ForegroundColor White
Write-Host "Successful: $successCount" -ForegroundColor Green
Write-Host "Failed: $failCount" -ForegroundColor Red
Write-Host "Total Duration: $([math]::Round($totalDuration, 2))s" -ForegroundColor White
Write-Host "Success Rate: $([math]::Round($successCount * 100.0 / $scripts.Count, 1))%" -ForegroundColor $(if ($failCount -eq 0) { "Green" } else { "Yellow" })

if ($failCount -eq 0) {
    Write-Title "Database Initialization Completed!"
    Write-Message "Smart Campus Management System is ready for use." "Green"
    Write-Host ""
    Write-Message "Default admin account:" "Cyan"
    Write-Message "  Username: admin001" "White"
    Write-Message "  Password: 123456" "White"
    Write-Message "  Email: admin001@university.edu.cn" "White"
} else {
    Write-Title "Database Initialization Partially Completed"
    Write-Message "Some scripts failed to execute, please check error messages." "Yellow"
}

Write-Host ""
Write-Message "Press Enter to exit..." "Gray"
Read-Host
