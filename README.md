## 说明
jdk17
### 进入部署目录
cd /dev-ops
### 运行
docker-compose -f docker-compose-app.yml up -d
### ldap导入用户数据
cat ./full-import.ldif | docker exec -i ldap-service ldapadd -x -H ldap://localhost:389 -D "cn=admin,dc=example,dc=com"
-w adminpassword

### 测试
登录页 http://117.72.98.133:7573/
### 登录
curl --request POST ^
--url http://117.72.98.133:7573/auth/login ^
--header "Accept: */*" ^
--header "Accept-Encoding: gzip, deflate, br" ^
--header "Connection: keep-alive" ^
--header "Content-Type: application/json" ^
--header "User-Agent: PostmanRuntime-ApipostRuntime/1.1.0" ^
--data "{\"type\":\"ldap\",\"username\":\"ldap_editor_1\",\"password\":\"ldap_editor_1\"}"

#### 输出
{
"code": "0000",
"data": {
"accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ",
"errorMessage": null,
"expiresIn": 7200,
"redirectUrl": null,
"tokenType": "Bearer",
"user": {
"id": 5,
"username": "ldap_editor_1",
"role": "EDITOR"
}
},
"info": "登录成功"
}

### 查询产品
curl --request GET ^
--url http://117.72.98.133:7573/products ^
--header "Accept: */*" ^
--header "Accept-Encoding: gzip, deflate, br" ^
--header "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ" ^
--header "Connection: keep-alive" ^
--header "User-Agent: PostmanRuntime-ApipostRuntime/1.1.0"

#### 输出
[
{ "id": 1, "name": "iPhone 15" },
{ "id": 2, "name": "MacBook Pro" },
{ "id": 3, "name": "iPad Air" },
{ "id": 4, "name": "Test" },
{ "id": 5, "name": "Test" },
{ "id": 6, "name": "Test" },
{ "id": 7, "name": "Test" },
{ "id": 8, "name": "Test" }
]

### 添加产品
curl --request POST ^
--url http://117.72.98.133:7573/products ^
--header "Accept: */*" ^
--header "Accept-Encoding: gzip, deflate, br" ^
--header "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ" ^
--header "Connection: keep-alive" ^
--header "Content-Type: application/json" ^
--header "User-Agent: PostmanRuntime-ApipostRuntime/1.1.0" ^
--data "{\"name\":\"Test333\"}"

#### 输出
{
"id": 10,
"name": "Test333"
}

### 修改产品
curl --request PUT ^
--url http://117.72.98.133:7573/products/10 ^
--header "Accept: */*" ^
--header "Accept-Encoding: gzip, deflate, br" ^
--header "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ" ^
--header "Connection: keep-alive" ^
--header "Content-Type: application/json" ^
--header "User-Agent: PostmanRuntime-ApipostRuntime/1.1.0" ^
--data "{\"name\":\"Tes32333t\"}"

#### 输出
{
"id": 10,
"name": "Tes32333t"
}

### 删除产品
curl --request DELETE ^
--url http://117.72.98.133:7573/products/10 ^
--header "Accept: */*" ^
--header "Accept-Encoding: gzip, deflate, br" ^
--header "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ" ^
--header "Connection: keep-alive" ^
--header "User-Agent: PostmanRuntime-ApipostRuntime/1.1.0"

#### 输出
删除成功

