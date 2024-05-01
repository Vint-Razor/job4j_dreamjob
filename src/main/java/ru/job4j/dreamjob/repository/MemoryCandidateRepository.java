package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();
    private final AtomicInteger counterId = new AtomicInteger(1);

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Ivan", "Java, Maven, GIT", 1));
        save(new Candidate(0, "Igor Yang", "Java, Maven, GIT, JUnit", 1));
        save(new Candidate(0, "Roman Petrov", "Java, GIT, JACOCO", 2));
        save(new Candidate(0, "Andrey Volkov", "Java, C#, Python", 3));
        save(new Candidate(0, "Aleksey Tolstoy", "Java, Lisp, Basic", 3));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(counterId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        candidate.setCreationDate(candidates.get(candidate.getId()).getCreationDate());
        return candidates.computeIfPresent(candidate.getId(), (id, oldVal) -> candidate) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
