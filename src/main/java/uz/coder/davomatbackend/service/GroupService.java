package uz.coder.davomatbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uz.coder.davomatbackend.db.GroupDatabase;
import uz.coder.davomatbackend.db.model.GroupDbModel;
import uz.coder.davomatbackend.model.Group;
import java.util.List;
import java.util.stream.Collectors;
import static uz.coder.davomatbackend.todo.Strings.THERE_IS_NO_SUCH_A_GROUP;

@Service
public class GroupService {
    private final GroupDatabase database;

    @Autowired
    public GroupService(GroupDatabase database) {
        this.database = database;
    }
    public Group save(Group group) {
        GroupDbModel save = database.save(new GroupDbModel(group.getTitle(), group.getCourseId()));
        return new Group(save.getId(), save.getTitle(), save.getCourseId());
    }
    public Group edit(Group group) {
        database.update(group.getId(), group.getTitle(), group.getCourseId());
        GroupDbModel save = database.findById(group.getId()).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_GROUP));
        return new Group(save.getId(), save.getTitle(), save.getCourseId());
    }
    public Group findById(long id) {
        GroupDbModel group = database.findById(id).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_GROUP));
        return new Group(group.getId(), group.getTitle(), group.getCourseId());
    }
    public int deleteById(long id) {
        if (database.existsById(id)){
            database.deleteById(id);
            return 1;
        }else {
            return 0;
        }
    }
    public List<Group> findAllGroupByCourseId(long courseId) {
        List<GroupDbModel> allByUserId = database.findAllByCourseId(courseId);
        return allByUserId.stream().map(item -> new Group(item.getId(), item.getTitle(), item.getCourseId())).collect(Collectors.toList());
    }

    public Page<Group> findAllGroupByCourseIdPaginated(long courseId, Pageable pageable) {
        Page<GroupDbModel> groupPage = database.findAllByCourseId(courseId, pageable);
        return groupPage.map(item -> new Group(item.getId(), item.getTitle(), item.getCourseId()));
    }
}