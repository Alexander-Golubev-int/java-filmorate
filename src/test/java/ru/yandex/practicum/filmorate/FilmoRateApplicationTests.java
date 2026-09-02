package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.storage.dal.dto.*;
import ru.yandex.practicum.filmorate.storage.dal.mappers.*;
import ru.yandex.practicum.filmorate.storage.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.storage.dal.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserRepository.class,
        FilmRepository.class,
        RowMapperUser.class,
        RowMapperFilm.class,
        RowMapperGenreDto.class,
        RowMapperAgeRating.class,
        PopularFilmRowMapper.class
})
class FilmoRateApplicationTests {

    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    @Test
    void testCreateAndFindUser() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("test-user@mail.ru");
        request.setLogin("testUser");
        request.setName("Test User");
        request.setBirthday(LocalDate.of(1990, 1, 1));

        UserDto created = userRepository.addNewUser(request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("test-user@mail.ru");
        assertThat(created.getLogin()).isEqualTo("testUser");

        Optional<UserDto> found = userRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("testUser");
        assertThat(found.get().getEmail()).isEqualTo("test-user@mail.ru");
    }

    @Test
    void testFindAllUsersAfterCreate() {
        NewUserRequest request = new NewUserRequest();
        request.setEmail("user2@mail.ru");
        request.setLogin("user2");
        request.setName("User Two");
        request.setBirthday(LocalDate.of(1992, 2, 2));

        userRepository.addNewUser(request);

        List<UserDto> users = userRepository.findAll();
        assertThat(users).isNotEmpty();
        assertThat(users).anyMatch(u -> "user2".equals(u.getLogin()));
    }

    @Test
    void testFindUserByIdNotFound() {
        Optional<UserDto> user = userRepository.findById(999999L);
        assertThat(user).isEmpty();
    }

    @Test
    void testCreateFilm() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Test Film");
        request.setDescription("Test Description");
        request.setReleaseDate(LocalDate.of(2000, 1, 1));
        request.setDuration(100);

        // MPA
        MpaDto mpa = new MpaDto();
        mpa.setId(1L);
        request.setMpa(mpa);

        // Genre
        GenreDto genre = new GenreDto();
        genre.setId(1L);
        request.setGenres(List.of(genre));

        FilmDto created = filmRepository.addNewFilm(request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Film");
        assertThat(created.getDescription()).isEqualTo("Test Description");
        assertThat(created.getDuration()).isEqualTo(100);

        // Проверяем, что mpa теперь объект
        assertThat(created.getMpa()).isNotNull();
        assertThat(created.getMpa().getId()).isEqualTo(1L);
        assertThat(created.getMpa().getName()).isNotBlank(); // например "G"

        // Проверяем жанры
        assertThat(created.getGenres()).isNotEmpty();
        assertThat(created.getGenres().get(0).getId()).isEqualTo(1L);
    }

    @Test
    void testFindFilmById() {
        // Сначала создаём фильм
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Find Me Film");
        request.setDescription("Description");
        request.setReleaseDate(LocalDate.of(2010, 5, 15));
        request.setDuration(90);

        MpaDto mpa = new MpaDto();
        mpa.setId(3L);
        request.setMpa(mpa);

        FilmDto created = filmRepository.addNewFilm(request);

        FilmDto found = filmRepository.findById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo("Find Me Film");
        assertThat(found.getMpa()).isNotNull();
        assertThat(found.getMpa().getId()).isEqualTo(3L);
    }

    @Test
    void testFindAllGenres() {
        List<GenreDto> genres = filmRepository.findAllGenre();
        assertThat(genres).isNotNull();
        assertThat(genres).isNotEmpty(); // обычно в схеме есть предустановленные жанры
    }

    @Test
    void testFindGenreById() {
        GenreDto genre = filmRepository.findGenreById(1L);
        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isNotBlank();
    }

    @Test
    void testFindAllMpa() {
        List<AgeRatingDto> mpaList = filmRepository.findAllMpa();
        assertThat(mpaList).isNotNull();
        assertThat(mpaList).isNotEmpty();
    }

    @Test
    void testFindMpaById() {
        AgeRatingDto mpa = filmRepository.findMpaById(1L);
        assertThat(mpa).isNotNull();
        assertThat(mpa.getId()).isEqualTo(1L);
        assertThat(mpa.getName()).isNotBlank();
    }

    @Test
    void testFindMostPopularFilms() {
        List<FilmDto> popular = filmRepository.findMostPopularFilms(10L);
        assertThat(popular).isNotNull();
    }

    @Test
    void testHasLike() {
        boolean result = filmRepository.hasLike(1L, 1L);
        assertThat(result).isFalse();
    }

    @Test
    void testFindAllFriends() {
        List<UserDto> friends = userRepository.findAllFriends(1L);
        assertThat(friends).isNotNull();
    }

    @Test
    void testFindAllFilms() {
        List<FilmDto> films = filmRepository.findAll();
        assertThat(films).isNotNull();
    }
}