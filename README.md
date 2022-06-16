# PrivateChat
Мой дипломный проект на тему "Система открытого распределния ключей".

В основу системы взят протокол Диффи-Хеллмана. Протокол уязвим от атаки "Человек по середине", поэтому добавлена аутентификация пользователей с помощью алгоритма ЭЦП DSA.

Сообщения шифруются алгоритом AES.

Система реализована на мобильной платформе Android , для хранения данных используется Fireabase database.
Окна регистрации, авторизации и списка доступных пользователей.
![image](https://user-images.githubusercontent.com/57949020/174154672-ab21b04f-43bd-44f2-bf39-6c2c4604c2de.png)
![image](https://user-images.githubusercontent.com/57949020/174154685-6c6d70cd-cb69-4fff-afbd-a4eb80100c84.png)
![image](https://user-images.githubusercontent.com/57949020/174154690-7ff5079d-349c-4b34-a607-f9913bb9e163.png)

Сообщение инициации чата.
![image](https://user-images.githubusercontent.com/57949020/174154717-871a1507-29eb-4791-9e9a-3236e2fcb69a.png)

Успешная аутентификация и выработка общего ключа шифрования.
![image](https://user-images.githubusercontent.com/57949020/174154737-b6b013fc-5c36-49f9-a4b8-e6d9a842848f.png)
![image](https://user-images.githubusercontent.com/57949020/174154745-575c8123-3829-4c70-ae7c-60f7d174607b.png)

Пример диалога.
![image](https://user-images.githubusercontent.com/57949020/174154757-c697f191-8783-410c-b00e-2508402b7a1b.png)
![image](https://user-images.githubusercontent.com/57949020/174154767-5703fbbd-ef5f-4d7a-a687-fe16b8cecc56.png)

