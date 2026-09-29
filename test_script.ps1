# Simple Test Script for ShopStream System

## Step 1: Check current products
Invoke-WebRequest -Uri "http://localhost:8080/api/products" -UseBasicParsing

## Step 2: Get a specific product to check its details
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1" -UseBasicParsing

## Step 3: Check current suggestions
Invoke-WebRequest -Uri "http://localhost:8080/api/suggestions/pricing" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/suggestions/reorder" -UseBasicParsing

## Step 4: Simulate a sale to trigger demand spike
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1/orders" -Method POST -UseBasicParsing

## Step 5: Simulate multiple sales to definitely trigger demand spike
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1/orders" -Method POST -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1/orders" -Method POST -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1/orders" -Method POST -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1/orders" -Method POST -UseBasicParsing

## Step 6: Check suggestions again - you should see new ones
Invoke-WebRequest -Uri "http://localhost:8080/api/suggestions/pricing" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/suggestions/reorder" -UseBasicParsing

## Step 7: Reduce stock to trigger low inventory event
Invoke-WebRequest -Uri "http://localhost:8080/api/products/1/stock" -Method PATCH -Body '{"stockLevel": 2}' -ContentType "application/json" -UseBasicParsing

## Step 8: Check suggestions again
Invoke-WebRequest -Uri "http://localhost:8080/api/suggestions/pricing" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/api/suggestions/reorder" -UseBasicParsing