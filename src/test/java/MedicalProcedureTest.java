import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MedicalProcedureTest {

    @DisplayName("Parse file")
    @Test
    void parse_WhenParseIsCorrect_ShouldExceptionWillBeNotThrown() {
        //given
        MedicalProcedure medicalProcedure = new MedicalProcedure();

        //when
        medicalProcedure.parse("src/test/java/resources/test-sheet.xlsx");

        //then
        assertDoesNotThrow(() -> {
        });
    }
}