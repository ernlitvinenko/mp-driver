package com.example.mpdriver.errors

sealed class AuthErrors(val displayText: String) {
    data object InvalidPhoneNumber : AuthErrors("Неверный формат номера телефона")
    data object NoProfileFounded : AuthErrors("Сотрудник не найден")
    data object InvalidCode : AuthErrors("Неверный код")
    data object ServerError : AuthErrors("Ошибка сервера")
}