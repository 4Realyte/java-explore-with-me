# Explore-with-me

Backend часть приложения, которое позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них.

Приложение состоит из 2-ух модулей, один из которых также состоит из саб-модулей: 
* **Ewm-service** - основной сервис, содержит всё необходимое для работы продукта
* **Stats-service** - Сервис статистики, хранит количество просмотров и позволяет делать различные выборки для анализа работы приложения
  - stats-server
  - stats-client
  - stats-dto


## Language and tools
* ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
* ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
* ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
* ![MapStruct](https://img.shields.io/badge/MapStruct-8A2BE2)
* ![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
* ![Google](https://img.shields.io/badge/google%20MAPS-4285F4?style=for-the-badge&logo=google&logoColor=white)
* ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
* ![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
## API Reference
API основного сервиса разделен на три части:
* публичная доступна без регистрации любому пользователю сети
* закрытая доступна только авторизованным пользователям
* административная — для администраторов сервиса.
### Примеры запросов
* Получение событий с возможностью фильтрации
```http
  GET /events 
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `text` | `string` | текст для поиска в содержимом аннотации и описании события |
| `categories` | `array[integer]` | Cписок идентификаторов категорий |
| `paid` | `boolean` | поиск только платных/бесплатных событий |
| `rangeStart` | `string` | дата и время не раньше которых должно произойти событие |
| `rangeEnd` | `string` | дата и время не позже которых должно произойти событие|
| `onlyAvailable` | `boolean` | только события у которых не исчерпан лимит запросов на участие|
| `sort` | `string` | Вариант сортировки: по дате события или по количеству просмотров|
| `from` | `integer` | количество событий, которые нужно пропустить для формирования текущего набора|
| `size` | `string` | количество событий в наборе|

