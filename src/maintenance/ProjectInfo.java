package maintenance;

public class ProjectInfo {
    private String name;
    private String path;
    private String version;
    private int linesOfCode;
    private String technicalDebt;
    private double testCoverage;
    private int numberOfClasses;
    
    public ProjectInfo(String name, String path) {
        this.name = name;
        this.path = path;
    }
    
    // Getters et Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public int getLinesOfCode() { return linesOfCode; }
    public void setLinesOfCode(int loc) { this.linesOfCode = loc; }
    
    public String getTechnicalDebt() { return technicalDebt; }
    public void setTechnicalDebt(String debt) { this.technicalDebt = debt; }
    
    public double getTestCoverage() { return testCoverage; }
    public void setTestCoverage(double coverage) { this.testCoverage = coverage; }
    
    public int getNumberOfClasses() { return numberOfClasses; }
    public void setNumberOfClasses(int n) { this.numberOfClasses = n; }
}