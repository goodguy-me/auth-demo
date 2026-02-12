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

### 登录

curl 'http://117.72.98.133:7573/auth/login' \
-H 'Accept: */*' \
-H 'Accept-Language: zh-CN,zh;q=0.9,en;q=0.8' \
-H 'Content-Type: application/json' \
-b '_pk_id.1.662e=ccc32f4ca55420e7.1754504763.; JSESSIONID.e80e1cc6=node010quzfjd7ityx1oksxmt1mzizd0.node0;
JSESSIONID.07631a22=node01bvl3aw37qibkqkegr22poyiu1.node0; JSESSIONID.1986d344=node01g407i5ca8rbj1w3jea49in26l0.node0;
JSESSIONID.0e92fa34=node01p15e0mw4xcjckn6go9tm38xn0.node0; JSESSIONID.41641b8e=node016k55cs150vbyzw9jxvwh13jx0.node0;
JSESSIONID.2e18cbbe=node0p4ojzvrxk9r7etz6x42a0dt0.node0; _pk_ses.1.662e=1' \
-H 'Origin: http://117.72.98.133:7573' \
-H 'Proxy-Connection: keep-alive' \
-H 'Referer: http://117.72.98.133:7573/' \
-H 'User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0
Safari/537.36' \
--data-raw '{"type":"ldap","username":"ldap_editor_1","password":"ldap_editor_1"}' \
--insecure
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

curl --request GET \
--url http://localhost:8082/products \
--header 'Accept: */*' \
--header 'Accept-Encoding: gzip, deflate, br' \
--header 'Authorization: Bearer
eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiRURJVE9SIiwidXNlcklkIjo5LCJ1c2VybmFtZSI6Imdvb2RndXktbWVfZ2l0aHViIiwic3ViIjoiZ29vZGd1eS1tZV9naXRodWIiLCJpYXQiOjE3NzA4MjQ3OTgsImV4cCI6MTc3MDgzMTk5OX0.QgD-6Gm3bBs3QPpxDtM-zvbRRKfRd0oNYMyXxmECISw' \
--header 'Connection: keep-alive' \
--header 'Cookie: JSESSIONID=E0E3D551F88A6EFA9F41E3926BAF21A8' \
--header 'User-Agent: PostmanRuntime-ApipostRuntime/1.1.0'

#### 输出

[
{
"id": 1,
"name": "iPhone 15"
},
{
"id": 2,
"name": "MacBook Pro"
},
{
"id": 3,
"name": "iPad Air"
},
{
"id": 4,
"name": "Test"
},
{
"id": 5,
"name": "Test"
},
{
"id": 6,
"name": "Test"
},
{
"id": 7,
"name": "Test"
},
{
"id": 8,
"name": "Test"
}
]

### 添加产品

curl --request POST \
--url http://117.72.98.133:7573/products \
--header 'Accept: */*' \
--header 'Accept-Encoding: gzip, deflate, br' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ' \
--header 'Connection: keep-alive' \
--header 'Content-Type: application/json' \
--header 'User-Agent: PostmanRuntime-ApipostRuntime/1.1.0' \
--data '{"name":"Test333"}'
#### 输出
{
"id": 10,
"name": "Test333"
}
### 修改产品
curl --request PUT \
--url http://117.72.98.133:7573/products/10 \
--header 'Accept: */*' \
--header 'Accept-Encoding: gzip, deflate, br' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ' \
--header 'Connection: keep-alive' \
--header 'Content-Type: application/json' \
--header 'User-Agent: PostmanRuntime-ApipostRuntime/1.1.0' \
--data '{"name":"Tes32333t"}'
#### 输出
{
"id": 10,
"name": "Tes32333t"
}
### 删除产品
curl --request DELETE \
--url http://117.72.98.133:7573/products/10 \
--header 'Accept: */*' \
--header 'Accept-Encoding: gzip, deflate, br' \
--header 'Authorization: Bearer  eyJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImxkYXBfZWRpdG9yXzEiLCJ1c2VySWQiOjUsInJvbGUiOiJFRElUT1IiLCJzdWIiOiJsZGFwX2VkaXRvcl8xIiwiaWF0IjoxNzcwOTMxMTA2LCJleHAiOjE3NzA5MzgzMDZ9._AQJR0hAsga6ZlCLGu_trAa413ksBQ-QmX9E6Lu0ZzQ' \
--header 'Connection: keep-alive' \
--header 'User-Agent: PostmanRuntime-ApipostRuntime/1.1.0'
#### 输出
删除成功

