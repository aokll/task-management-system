import com.example.demo.Entity.Difficulty;
import com.example.demo.Entity.Status;
import com.example.demo.Entity.Task;
import com.example.demo.Entity.User;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.TaskAnalysisService;
import com.example.demo.service.TaskMapper;
import com.example.demo.service.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @Mock private TaskMapper taskMapper;
    @Mock private TaskAnalysisService analysisService;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp(){
        // Имитируем авторизацию пользователя "amigo" перед каждым тестом
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("amigo", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void deleteTask_Succes(){
        Long taskId = 1L;
        User owner = new User();
        owner.setUsername("amigo");
        Task task = new Task();
        task.setOwner(owner);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.deleteTask(taskId);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void markAsDone_ChangesStatusAndCallsAsync(){
        Long taskId = 5L;
        User owner = new User();
        owner.setUsername("amigo");
        Task task = new Task();
        task.setId(taskId);
        task.setOwner(owner);
        task.setStatus(Status.NEEDS_TO_BE_DECIDED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.markAsDone(taskId);

        assertEquals(Status.ANALYZING, task.getStatus());
        verify(taskRepository).saveAndFlush(task);
        verify(analysisService).analyzeTask(taskId);
    }

    @Test
    void updateTask_ShouldUpdateFields(){
        Long taskId = 1L;
        User owner = new User();
        owner.setUsername("amigo");
        Task task = new Task();
        task.setOwner(owner);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.updateTask(taskId, "New Title", 10, "New Topic", Difficulty.HARD);

        assertEquals("New Title", task.getTitle());
        assertEquals(Difficulty.HARD, task.getDifficulty());
        verify(taskRepository).save(task);
    }
}
