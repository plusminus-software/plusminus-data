package software.plusminus.patch.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.check.util.JsonUtil;
import software.plusminus.patch.helpers.TestDto;
import software.plusminus.patch.service.patcher.Patcher;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PatchServiceTest {

    @Spy
    private List<Patcher> patchers = new ArrayList<>();
    @Mock
    private Patcher patcher1;
    @Mock
    private Patcher patcher2;

    @InjectMocks
    private PatchService patchService = new PatchService();

    @Before
    public void setUp() {
        patchers.clear();
        patchers.add(patcher1);
        patchers.add(patcher2);
    }

    @Test
    public void patch() {
        TestDto expected = JsonUtil.fromJson("/json/test-dto.json", TestDto.class);
        TestDto target = JsonUtil.fromJson("/json/test-dto.json", TestDto.class);
        TestDto patch = getPatch();

        patchService.patch(patch, target);

        assertThat(target).isEqualToIgnoringGivenFields(expected, "myField");
        assertThat(target.getMyField()).isEqualTo("patched");
    }

    @Test
    public void patch_CallsPatchers() {
        TestDto target = JsonUtil.fromJson("/json/test-dto.json", TestDto.class);
        TestDto patch = getPatch();

        patchService.patch(patch, target);

        verify(patcher1).patch(patch, target);
        verify(patcher2).patch(patch, target);
    }

    private TestDto getPatch() {
        TestDto testDto = new TestDto();
        testDto.setMyField("patched");
        return testDto;
    }

}