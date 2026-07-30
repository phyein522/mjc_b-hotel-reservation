[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"

$envPath = Join-Path $PSScriptRoot ".env"
$values = [ordered]@{}

if (Test-Path -LiteralPath $envPath) {
    foreach ($line in Get-Content -LiteralPath $envPath) {
        if ($line -match "^\s*([^#][^=]*)=(.*)$") {
            $values[$matches[1].Trim()] = $matches[2]
        }
    }
}

$defaultMailUsername = $values["MAIL_USERNAME"]
$mailPrompt = if ([string]::IsNullOrWhiteSpace($defaultMailUsername)) {
    "Gmail address used to send verification codes"
} else {
    "Gmail address used to send verification codes [$defaultMailUsername]"
}

$mailUsername = Read-Host $mailPrompt
if ([string]::IsNullOrWhiteSpace($mailUsername)) {
    $mailUsername = $defaultMailUsername
}
if ($mailUsername -notmatch "^[^@\s]+@gmail\.com$") {
    throw "Enter a valid Gmail address."
}

Write-Host "Enter the 16-character Google app password. Your normal Google password will not work."
$secureMailPassword = Read-Host "Google app password" -AsSecureString
$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureMailPassword)
try {
    $mailPassword = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
}
$mailPassword = $mailPassword -replace "\s", ""
if ($mailPassword.Length -ne 16) {
    throw "Google app passwords contain 16 characters."
}

$googleClientId = (Read-Host "Google OAuth Web client ID").Trim()
if ($googleClientId -notmatch "^[0-9]+-[A-Za-z0-9_-]+\.apps\.googleusercontent\.com$") {
    throw "Enter a Google OAuth Web application client ID."
}

if ([string]::IsNullOrWhiteSpace($values["EMAIL_VERIFICATION_SECRET"])) {
    $bytes = New-Object byte[] 48
    [Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
    $values["EMAIL_VERIFICATION_SECRET"] = [Convert]::ToBase64String($bytes)
}

$values["MAIL_USERNAME"] = $mailUsername
$values["MAIL_PASSWORD"] = $mailPassword
$values["MAIL_FROM"] = $mailUsername
$values["GOOGLE_CLIENT_ID"] = $googleClientId

$orderedKeys = @(
    "MAIL_HOST",
    "MAIL_PORT",
    "MAIL_USERNAME",
    "MAIL_PASSWORD",
    "MAIL_FROM",
    "EMAIL_VERIFICATION_SECRET",
    "GOOGLE_CLIENT_ID"
)

$output = foreach ($key in $orderedKeys) {
    if ($values.Contains($key) -and -not [string]::IsNullOrWhiteSpace($values[$key])) {
        "$key=$($values[$key])"
    }
}

Set-Content -LiteralPath $envPath -Value $output -Encoding UTF8
Write-Host ""
Write-Host "Authentication settings were saved to $envPath."
Write-Host "Restart the Spring Boot server to apply them."
