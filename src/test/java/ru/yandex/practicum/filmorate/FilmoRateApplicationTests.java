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

        Optional<UserDto> found = userRepository.findById(created.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo("testUser");
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

        AgeRatingDto mpa = new AgeRatingDto();
        mpa.setId(1L);
        request.setMpa(mpa);

        GenreDto genre = new GenreDto();
        genre.setId(1L);
        request.setGenres(List.of(genre));

        FilmDto created = filmRepository.addNewFilm(request);

        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Test Film");
    }

    @Test
    void testFindAllGenres() {
        List<GenreDto> genres = filmRepository.findAllGenre();
        assertThat(genres).isNotNull();
    }

    @Test
    void testFindAllMpa() {
        List<AgeRatingDto> mpa = filmRepository.findAllMpa();
        assertThat(mpa).isNotNull();
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