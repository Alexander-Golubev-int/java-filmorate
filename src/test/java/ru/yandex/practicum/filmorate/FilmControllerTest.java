package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import ru.yandex.practicum.filmorate.controller.FilmController;


@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmControllerTest {
	FilmController filmController = new FilmController();


	//Не совсем понял как покрывать автотетсами сами анотации, т.к. весь проект завязан на них
	//Делал прогон через постман и JSON-файлик и все успешно проходило
	//Мне нужно сделать автотесты на сам контроллер и анотации? Просто логики у меня особо в этих классах то и нет
	//и не совсем понимаю как отправлять запрос через тестовый класс, ведь http методы, как в прошлом задании мы тут не
	//поднимаем
	@Test
	@Order(1)
	void getEmptyListFilms() {
		Assertions.assertTrue(filmController.getFilms().isEmpty());
	}


}
