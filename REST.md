GitHub - users

GET /users - list of users
GET /users/:username - user info (/users/coursar)
POST /users
PUT/PATCH /users/:username
DELETE /users/:username
POST /users/:username/block

1. Вложенность ресурсов
2. Нетиповые операции

Contract: внешнее API

GET /users/:username/repos
GET /users/:username/repos/:repoid

GET /users/:username/repos/:repoid/branches
GET /users/:username/repos/:repoid/branches/:branchname

GET /users/:username/repos/:repoid/branches/:branchname/commits
GET /users/:username/repos/:repoid/branches/:branchname/commits/:commit

GET /users/:username/repos/:repoid/branches/:branchname/commits/:commit/comments
GET /users/:username/repos/:repoid/branches/:branchname/commits/:commit/comments/:commentId

GET /top/repos -> [...]

GET /search/repos?q=...

POST /users.block
