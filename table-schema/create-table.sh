aws  dynamodb create-table --cli-input-json file://matchInfo.json --region ap-south-1 --endpoint-url http://localhost:8000
aws  dynamodb create-table --cli-input-json file://userInfo.json --region ap-south-1 --endpoint-url http://localhost:8000
aws  dynamodb create-table --cli-input-json file://votingInfo.json --region ap-south-1 --endpoint-url http://localhost:8000
aws  dynamodb create-table --cli-input-json file://userResult.json --region ap-south-1 --endpoint-url http://localhost:8000


