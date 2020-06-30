package io.github.w7mike.logic;


import io.github.w7mike.model.JobGroup;
import io.github.w7mike.model.JobGroupsRepository;
import io.github.w7mike.model.JobRepository;
import io.github.w7mike.model.projection.GroupReadModel;
import io.github.w7mike.model.projection.GroupWriteModel;

import java.util.Set;
import java.util.stream.Collectors;

public class JobGroupsService {

    private JobGroupsRepository groupsRepository;
    private JobRepository jobRepository;

    public JobGroupsService(final JobGroupsRepository groupsRepository, final JobRepository jobRepository) {
        this.groupsRepository = groupsRepository;
        this.jobRepository = jobRepository;
    }

    public GroupReadModel createGroup(final GroupWriteModel source){
        JobGroup result = groupsRepository.save(source.toGroup());
        return new GroupReadModel(result);
    }

    public Set<GroupReadModel> readAll(){
        return groupsRepository.findAll()
                .stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toSet());
    }

    public void toggleGroup(Integer groupId){
        if (jobRepository.existsByCompleteIsFalseAndJobGroup_Id(groupId)){
            throw new IllegalStateException("Group contains uncompleted jobs, please complete all jobs first");
        }

        JobGroup result = groupsRepository.findById(groupId)
                .orElseThrow(()-> new IllegalArgumentException("Group with given id does not exists"));
        result.setComplete(!result.isComplete());
        groupsRepository.save(result);
    }
}
