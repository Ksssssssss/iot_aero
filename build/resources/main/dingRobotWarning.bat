@echo off
cd /d "D:\soft\curl-7.71.1\bin"
set "url=https://oapi.dingtalk.com/robot/send?access_token=b74a8795d7ce4c87aec78d9fb483f7c8d365ddb9cc8be4e7bf497cf359a5659b"
curl.exe -XPOST %url% -H "Content-Type: application/json"  -d "{\"msgtype\":\"text\",\"text\":{\"content\":\"%~1\"}}"
exit