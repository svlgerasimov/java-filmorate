# Filmorate

***Учебный проект Яндекс Практикума.***

Backend социальной сети для оценки и подбора фильмов.

---
### Стек технологий

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/spring%20Boot-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

---
### Хранение данных

Для хранения данных используется встраиваемая база данных H2.

В образовательных целях в проекте не используется ORM, 
а доступ к данным осуществляется через JdbcTemplate с нативными SQL-запросами.


![ER-диаграмма](/assets/images/schema.png)
