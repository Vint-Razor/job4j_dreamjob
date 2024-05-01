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
    private final Map<Integer, Vacancy> vacancies = new ConcurrentHashMap<>();
    private final AtomicInteger nextId = new AtomicInteger(1);

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "ИП \"ТАРАН\", требования: без опыта", true, 1));
        save(new Vacancy(0, "Junior Java Developer", "ЧОП \"ВАРЯГ\", требования: 1 год опыта", true, 1));
        save(new Vacancy(0, "Junior+ Java Developer", "Infinity Solution, требования: 2 года опыта", true, 1));
        save(new Vacancy(0, "Middle Java Developer", "Теньков, требования: 2-3 года опыта", true, 1));
        save(new Vacancy(0, "Middle+ Java Developer", "Сбер, требования: 3 года опыта", true, 2));
        save(new Vacancy(0, "Senior Java Developer", "Яндекс, требования: 3-6 лет опыта", true, 3));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        vacancy.setCreationDate(vacancies.get(vacancy.getId()).getCreationDate());
        return vacancies.computeIfPresent(vacancy.getId(), (key, val) -> vacancy) != null;
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
