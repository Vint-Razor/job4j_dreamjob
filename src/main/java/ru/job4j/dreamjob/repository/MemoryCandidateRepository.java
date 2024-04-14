package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.util.*;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {
    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private Map<Integer, Candidate> candidates = new HashMap<>();
    private int counterId = 1;

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Ivan", "Java, Maven, GIT", new Date()));
        save(new Candidate(0, "Igor Yang", "Java, Maven, GIT, JUnit", new Date()));
        save(new Candidate(0, "Roman Petrov", "Java, GIT, JOCOCO", new Date()));
        save(new Candidate(0, "Andrey Volkov", "Java, C#, Python", new Date()));
        save(new Candidate(0, "Aleksey Tolstoy", "Java, Lisp, Basic", new Date()));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(counterId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldVal) -> new Candidate(id, candidate.getName(),
                        candidate.getDescription(), candidate.getCreationDate())) != null;
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