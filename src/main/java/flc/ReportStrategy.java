package flc;

import java.util.List;

/**
 * Strategy Pattern — defines the algorithm contract for report generation.
 * Concrete strategies (AttendanceReportStrategy, IncomeReportStrategy) can be
 * swapped at runtime without changing FurzefieldLC.
 * Satisfies Open/Closed Principle: open for new report types, closed for modification.
 */
public interface ReportStrategy {
    void generate(List<Timetable> timetables);
}
