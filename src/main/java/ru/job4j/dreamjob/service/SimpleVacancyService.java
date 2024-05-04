package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleVacancyService implements VacancyService {
    private final VacancyRepository vacancyRepository;
    private final FileService fileService;

    public SimpleVacancyService(VacancyRepository sql2oVacancyRepository, FileService fileService) {
        this.vacancyRepository = sql2oVacancyRepository;
        this.fileService = fileService;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto image) {
        saveNewFile(vacancy, image);
        return vacancyRepository.save(vacancy);
    }

    private void saveNewFile(Vacancy vacancy, FileDto image) {
        File file = fileService.save(image);
        vacancy.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        boolean isDeleted = false;
        Optional<Vacancy> fileOptional = findById(id);
        if (fileOptional.isPresent()) {
            isDeleted = vacancyRepository.deleteById(id);
            fileService.deleteById(fileOptional.get().getFileId());
        }
        return isDeleted;
    }

    @Override
    public boolean update(Vacancy vacancy, FileDto image) {
        boolean isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return vacancyRepository.update(vacancy);
        }
        int oldFileId = vacancy.getFileId();
        saveNewFile(vacancy, image);
        boolean isUpdate = vacancyRepository.update(vacancy);
        fileService.deleteById(oldFileId);
        return isUpdate;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}
