# Simple Crawler

## Stack ##
 
 * http4s - сервер
 * tapir - эндпоинты
 * laminar - front app
 * htmlunit - загрузка веб страниц 

## Модули ##
 * **crawler-core** основной сервис получения title страниц
 * **сrawler-shared** обшие ресурсы сервера, ядра и веб приложения, такие как типы данных, эндпоинты
 * **crawler-server** API server
 * **crawler-front** веб приложение

## Endpoints ##

 * http://localhost:8080/  - front application
 * **GET** http://localhost:8080/docs/ - swagger docs
 * **GET** http://localhost:8080/api/capture/title/flat?url=https://ya.ru - получение title из одной 
страницы (результат простой текст)
 * **GET** http://localhost:8080/api/capture/title?url=https://ya.ru - получение title из одной
   страницы (развернутый результат)
 * **GET** http://localhost:8080/api/capture/title/csv?urls=https://ya.ru,https://google.com - получение нескольких 
 tiltle-ов, на входе URL через запятую, на выходе CSV -> Url,Title,Status,Error
 * **POST** http://localhost:8080/api/capture/title - тело в json (io.lyubin.crawler.shared.domain.RichRequest) ответ
     json (io.lyubin.crawler.shared.domain.RichResponse)
 

## Запуск ##
```shell
sbt "project crawler-server" run
```

## TODO ##
 * запихать все в docker
 * имплементировать proxy, сейчас просто заглушка
 * добавить тесты в модуль server 
