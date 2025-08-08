package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Vibe Java Developer", "Считает себя программистом. Но без ИИ не может написать даже \"Hello World!\".", true));
        save(new Vacancy(0, "Intern Java Developer", "Имеет только базовые понятия языка, но есть ОГРОМНОЕ желание учиться.", false));
        save(new Vacancy(0, "Junior Java Developer", "Что-то где-то знает.", true));
        save(new Vacancy(0, "Junior+ Java Developer", "Может что-то написать, но за ним нужен постоянный контроль.", false));
        save(new Vacancy(0, "Middle Java Developer", "Уже опытный разработчик. Контроль не требуется.", true));
        save(new Vacancy(0, "Middle+ Java Developer", "\"Мотёрый волк\". Может спорить с синьёром.", false));
        save(new Vacancy(0, "Senior Java Developer", "\"Король, царь и Бог\".", true));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.incrementAndGet());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getVisible())) != null;
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