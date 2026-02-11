# API Test Script

Write-Host "=== API Test Started ===" -ForegroundColor Green

# 1. Signup
Write-Host "`n[1] Testing Signup..." -ForegroundColor Yellow
$signupBody = @{
    username = "testuser"
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

try {
    $signupResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/signup" -Method Post -ContentType "application/json" -Body $signupBody
    Write-Host "Signup Success: $($signupResponse.username)" -ForegroundColor Green
} catch {
    Write-Host "Signup failed (user may already exist)" -ForegroundColor Yellow
}

# 2. Login
Write-Host "`n[2] Testing Login..." -ForegroundColor Yellow
$loginBody = @{
    username = "testuser"
    password = "password123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/auth/login" -Method Post -ContentType "application/json" -Body $loginBody
$accessToken = $loginResponse.accessToken
Write-Host "Login Success! Token acquired" -ForegroundColor Green

# 3. Create Post
Write-Host "`n[3] Testing Create Post..." -ForegroundColor Yellow
$postBody = @{
    title = "Test Post"
    content = "This is a test post content."
} | ConvertTo-Json

$headers = @{
    Authorization = "Bearer $accessToken"
}

$postResponse = Invoke-RestMethod -Uri "http://localhost:8080/posts" -Method Post -Headers $headers -ContentType "application/json" -Body $postBody
Write-Host "Post Created! ID: $($postResponse.id)" -ForegroundColor Green

# 4. Get All Posts
Write-Host "`n[4] Testing Get All Posts..." -ForegroundColor Yellow
$listResponse = Invoke-RestMethod -Uri "http://localhost:8080/posts" -Method Get -Headers $headers
Write-Host "Total Posts: $($listResponse.totalCount)" -ForegroundColor Green
foreach ($post in $listResponse.posts) {
    Write-Host "  - [$($post.id)] $($post.title) by $($post.authorName) (likes: $($post.likeCount))" -ForegroundColor Cyan
}

# 5. Get Post Detail
Write-Host "`n[5] Testing Get Post Detail..." -ForegroundColor Yellow
$detailResponse = Invoke-RestMethod -Uri "http://localhost:8080/posts/$($postResponse.id)" -Method Get -Headers $headers
Write-Host "Title: $($detailResponse.title)" -ForegroundColor Cyan
Write-Host "Content: $($detailResponse.content)" -ForegroundColor Cyan
Write-Host "Author: $($detailResponse.authorName)" -ForegroundColor Cyan
Write-Host "Likes: $($detailResponse.likeCount)" -ForegroundColor Cyan

Write-Host "`n=== All Tests Passed ===" -ForegroundColor Green
Write-Host "`nAPI Documentation: See API_SPEC.md" -ForegroundColor Yellow
