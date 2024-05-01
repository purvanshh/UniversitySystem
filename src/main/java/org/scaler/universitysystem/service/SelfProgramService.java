package org.scaler.universitysystem.service;

import org.scaler.universitysystem.exceptions.DepartmentNotFoundException;
import org.scaler.universitysystem.exceptions.ProgramNotFoundException;
import org.scaler.universitysystem.models.Department;
import org.scaler.universitysystem.models.Program;
import org.scaler.universitysystem.repository.DepartmentRepository;
import org.scaler.universitysystem.repository.ProgramRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SelfProgramService implements ProgramService {
    private ProgramRepository programRepository;
    private DepartmentRepository departmentRepository;

    private DepartmentService departmentService;

    public SelfProgramService(ProgramRepository programRepository, DepartmentRepository departmentRepository, DepartmentService departmentService) {
        this.programRepository = programRepository;
        this.departmentRepository = departmentRepository;
        this.departmentService = departmentService;
    }


    @Override
    public Program getProgramById(Long id) {

        Optional<Program> optionalProgram = programRepository.findById(id);
        if (optionalProgram.isEmpty()) {
            throw new ProgramNotFoundException(id);
        }
        return optionalProgram.get();
    }

    @Override
    public Program createProgram(Program program) {
        Department department = program.getDepartment();
        if (department.getId()!=null){
            Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
            if (optionalDepartment.isEmpty()) {
                throw new DepartmentNotFoundException(department.getId());
            }
        }
        if (department.getId() == null) {
           department = departmentRepository.save(department);
         //   department = departmentService.createDepartment(department);
        }
        Optional<Department> optionalDepartment = departmentRepository.findById(department.getId());
      //  department.getPrograms().add(program);
        program.setDepartment(optionalDepartment.get());



        return programRepository.save(program);
    }

    @Override
    public Program updateProgram(Long id, Program program) {
        Optional<Program> optionalProgram = programRepository.findById(id);
        if (optionalProgram.isPresent()) {
            return programRepository.save(update(optionalProgram.get(), program));
        } else {
            throw new ProgramNotFoundException(id);
        }
    }

    @Override
    public void deleteProgram(Long id) {
        Optional<Program> program = programRepository.findById(id);
        if (program.isEmpty()) {
            throw new ProgramNotFoundException(id);
        }
        programRepository.deleteById(id);
    }

    @Override
    public List<Program> getProgramsByDepartment(Long departmentId) {
        Optional<Department> department = departmentRepository.findById(departmentId);
        if (department.isEmpty()) {
            throw new DepartmentNotFoundException(departmentId);
        }
        return programRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }

    public Program update(Program existingProgram, Program updatedProgram) {
        if(updatedProgram.getName() != null)
            existingProgram.setName(updatedProgram.getName());
        if(updatedProgram.getDuration() != 0){
            existingProgram.setDuration(updatedProgram.getDuration());
        }
        if(updatedProgram.getDepartment() != null){
            existingProgram.setDepartment(updatedProgram.getDepartment());
        }
        if(updatedProgram.getDegreeLevel() != null){
            existingProgram.setDegreeLevel(updatedProgram.getDegreeLevel());
        }
        return programRepository.save(existingProgram);
    }
}
