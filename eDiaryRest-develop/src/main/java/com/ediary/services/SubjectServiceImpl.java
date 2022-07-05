package com.ediary.services;

import com.ediary.domain.Subject;
import com.ediary.repositories.SubjectRepository;
import com.ediary.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;


    @Override
    public String getNameById(Long subjectId) {

        Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);

        if(!subjectOptional.isPresent()) {
            throw new NotFoundException("Subject Not Found.");
        }
        return subjectOptional.get().getName();
    }
}
