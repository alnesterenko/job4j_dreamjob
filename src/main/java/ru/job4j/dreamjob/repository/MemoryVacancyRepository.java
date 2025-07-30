package ru.job4j.dreamjob.repository;

import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MemoryVacancyRepository implements VacancyRepository {

    private static final MemoryVacancyRepository INSTANCE = new MemoryVacancyRepository();

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Vibe Java Developer", "Считает себя программистом. Но без ИИ не может написать даже \"Hello World!\"."));
        save(new Vacancy(0, "Intern Java Developer", "Имеет только базовые понятия языка, но есть ОГРОМНОЕ желание учиться."));
        save(new Vacancy(0, "Junior Java Developer", "Что-то где-то знает."));
        save(new Vacancy(0, "Junior+ Java Developer", "Может что-то написать, но за ним нужен постоянный контроль."));
        save(new Vacancy(0, "Middle Java Developer", "Уже опытный разработчик. Контроль не требуется."));
        save(new Vacancy(0, "Middle+ Java Developer", "\"Мотёрый волк\". Может спорить с синьёром."));
        save(new Vacancy(0, "Senior Java Developer", "\"Король, царь и Бог\"."));
    }

    public static MemoryVacancyRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public void deleteById(int id) {
        vacancies.remove(id);
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}