# =====================================================
# Smart Campus Management System - Database Initialization Script
# File: Execute-All-Scripts.ps1
# Description: PowerShell script to execute all SQL scripts automatically
# Version: 2.0.0 (Optimized)
# Created: 2025-01-27
# Encoding: UTF-8
# =====================================================

# Set console encoding to UTF-8
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding = [System.Text.Encoding]::UTF8

# Get script directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path

# Database connection configuration
$dbHost = "localhost"
$dbPort = "3306"
$dbUser = "root"
$dbPassword = "xiaoxiao123"
$dbName = "campus_management_db"

# Optimized version script list (Recommended)
$sqlFilesOptimized = @(
    "01_create_complete_tables.sql",
    "02_insert_large_scale_data.sql",
    "11_optimized_data_generation.sql",
    "12_data_validation_and_statistics.sql"
)

# Original version script list (Backup)
$sqlFilesOriginal = @(
    "01_create_complete_tables.sql",
    "02_insert_large_scale_data.sql",
    "08_complete_data_generation_fixed.sql",
    "09_business_data_generation_fixed.sql",
    "10_financial_and_other_data_fixed.sql",
    "07_data_validation_and_statistics.sql"
)

# Function: Display colored message
function Write-ColorMessage {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

# Function: Display title
function Write-Title {
    param([string]$Title)
    Write-Host ""
    Write-Host "=====================================================" -ForegroundColor Cyan
    Write-Host $Title -ForegroundColor Cyan
    Write-Host "=====================================================" -ForegroundColor Cyan
    Write-Host ""
}

# Function: Check MySQL availability
function Test-MySQLConnection {
    try {
        $mysqlArgs = @(
            "-h$dbHost",
            "-P$dbPort",
            "-u$dbUser",
            "-p$dbPassword",
            "-e",
            "SELECT 1;"
        )

        $null = & mysql $mysqlArgs 2>$null
        return $LASTEXITCODE -eq 0
    }
    catch {
        return $false
    }
}

# Function: Execute SQL file
function Execute-SQLFile {
    param(
        [string]$FilePath,
        [string]$FileName
    )

    Write-ColorMessage "Executing: $FileName" "Yellow"

    if (-not (Test-Path $FilePath)) {
        Write-ColorMessage "X File not found: $FilePath" "Red"
        return $false
    }

    try {
        $startTime = Get-Date

        # Use Get-Content and pipe to mysql instead of redirection
        $mysqlArgs = @(
            "-h$dbHost",
            "-P$dbPort",
            "-u$dbUser",
            "-p$dbPassword"
        )

        # Execute command using Get-Content and pipeline
        $output = Get-Content $FilePath | & mysql $mysqlArgs 2>&1
        $endTime = Get-Date
        $duration = ($endTime - $startTime).TotalSeconds

        if ($LASTEXITCODE -eq 0) {
            Write-ColorMessage "√ $FileName executed successfully (Duration: $([math]::Round($duration, 2))s)" "Green"
            return $true
        } else {
            Write-ColorMessage "X $FileName execution failed" "Red"
            Write-ColorMessage "Error: $output" "Red"
            return $false
        }
    }
    catch {
        Write-ColorMessage "X $FileName execution exception: $($_.Exception.Message)" "Red"
        return $false
    }
}

# Function: Display execution statistics
function Show-ExecutionStats {
    param(
        [int]$TotalFiles,
        [int]$SuccessCount,
        [int]$FailCount,
        [datetime]$StartTime,
        [datetime]$EndTime
    )

    $totalDuration = ($EndTime - $StartTime).TotalSeconds

    Write-Title "Execution Statistics Report"
    Write-Host "Total Files: $TotalFiles" -ForegroundColor White
    Write-Host "Successful: $SuccessCount" -ForegroundColor Green
    Write-Host "Failed: $FailCount" -ForegroundColor Red
    Write-Host "Total Duration: $([math]::Round($totalDuration, 2))s" -ForegroundColor White
    Write-Host "Success Rate: $([math]::Round($SuccessCount * 100.0 / $TotalFiles, 1))%" -ForegroundColor $(if ($FailCount -eq 0) { "Green" } else { "Yellow" })
}

# Main program starts
Write-Title "Smart Campus Management System - Database Initialization"

# Check MySQL connection
Write-ColorMessage "Checking MySQL connection..." "Yellow"
if (-not (Test-MySQLConnection)) {
    Write-ColorMessage "X Cannot connect to MySQL database, please check:" "Red"
    Write-ColorMessage "  1. MySQL service is running" "Red"
    Write-ColorMessage "  2. Connection parameters are correct (Host: $dbHost, Port: $dbPort, User: $dbUser)" "Red"
    Write-ColorMessage "  3. User password is correct" "Red"
    exit 1
}
Write-ColorMessage "√ MySQL connection is normal" "Green"

# Select execution plan
Write-Host ""
Write-ColorMessage "Please select execution plan:" "Cyan"
Write-ColorMessage "1. Optimized version (Recommended) - Efficient batch data generation" "White"
Write-ColorMessage "2. Original version (Backup) - Step-by-step data generation" "White"
Write-ColorMessage "3. Exit" "White"
Write-Host ""

do {
    $choice = Read-Host "Please enter your choice (1-3)"
    switch ($choice) {
        "1" {
            $sqlFiles = $sqlFilesOptimized
            $versionName = "Optimized Version"
            break
        }
        "2" {
            $sqlFiles = $sqlFilesOriginal
            $versionName = "Original Version"
            break
        }
        "3" {
            Write-ColorMessage "Exit execution" "Yellow"
            exit 0
        }
        default {
            Write-ColorMessage "Invalid choice, please enter 1, 2 or 3" "Red"
        }
    }
} while ($choice -notin @("1", "2", "3"))

Write-Title "Starting execution of $versionName scripts"

# Confirm execution
Write-ColorMessage "About to execute the following script files:" "Yellow"
foreach ($file in $sqlFiles) {
    Write-ColorMessage "  • $file" "White"
}
Write-Host ""

$confirm = Read-Host "Confirm execution? (y/N)"
if ($confirm -ne "y" -and $confirm -ne "Y") {
    Write-ColorMessage "Execution cancelled" "Yellow"
    exit 0
}

# Start executing scripts
$startTime = Get-Date
$successCount = 0
$failCount = 0

Write-Title "Executing SQL Scripts"

foreach ($file in $sqlFiles) {
    $filePath = Join-Path $scriptPath $file

    if (Execute-SQLFile -FilePath $filePath -FileName $file) {
        $successCount++
    } else {
        $failCount++

        # Ask whether to continue
        Write-Host ""
        $continue = Read-Host "Script execution failed, continue with remaining scripts? (y/N)"
        if ($continue -ne "y" -and $continue -ne "Y") {
            Write-ColorMessage "User chose to stop execution" "Yellow"
            break
        }
    }

    Write-Host ""
}

$endTime = Get-Date

# Display execution results
Show-ExecutionStats -TotalFiles $sqlFiles.Count -SuccessCount $successCount -FailCount $failCount -StartTime $startTime -EndTime $endTime

if ($failCount -eq 0) {
    Write-Title "Database Initialization Completed!"
    Write-ColorMessage "Smart Campus Management System is ready for use." "Green"
    Write-Host ""
    Write-ColorMessage "Default admin account:" "Cyan"
    Write-ColorMessage "  Username: admin001" "White"
    Write-ColorMessage "  Password: 123456" "White"
    Write-ColorMessage "  Email: admin001@university.edu.cn" "White"
} else {
    Write-Title "Database Initialization Partially Completed"
    Write-ColorMessage "Some scripts failed to execute, please check error messages and handle manually." "Yellow"
}

Write-Host ""
Write-ColorMessage "Press any key to exit..." "Gray"
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
