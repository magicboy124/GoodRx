package com.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;



/**
 * Reporter that generates a single-page HTML report of the suite test results.
 * <p>
 * Based on TestNG built-in implementation: org.testng.reporters.EmailableReporter2
 * </p>
 */
public class CustomReporter implements IReporter {

    private static final Logger LOG = Logger.getLogger(CustomReporter.class);
    private static String timeZone = "GMT-4";
    private static SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
    private static SimpleDateFormat sdftime = new SimpleDateFormat("HH:mm:ss");
    private static NumberFormat integerFormat = NumberFormat.getIntegerInstance();
    protected PrintWriter writer;
    protected List<SuiteResult> suiteResults = Lists.newArrayList();
    private StringBuilder buffer = new StringBuilder();

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
           for ( ISuite suite : suites ) {
            suiteResults.add( new SuiteResult( suite ) );
        }
        
        Collections.sort(suiteResults, SuiteResult.SUITE_COMPARATOR);

       // writer = null;
        File file = new File("test-output\\report.html");
        file.getParentFile().mkdirs();

       try {
		writer = new PrintWriter(file);
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

       
        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();
 
        writer.close();
    }

    protected void writeDocumentStart() {
        writer.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writer.print("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    }

    protected void writeHead() {
        writer.print("<head>");
        writer.print("<title>TestNG Report</title>");
        writeStylesheet();
        writer.print("</head>");
    }

    protected void writeStylesheet() {
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("th,td {border:1px solid #009;padding:.25em .5em}");
        writer.print("th {vertical-align:bottom}");
        writer.print("th.suiteHeader {font-size:18px;background-color:#888}");
        writer.print("th.testcaseHeader {font-size:14px;background-color:#ccc}");
        writer.print(".testClassName {background-color:#ccc;}");
        writer.print(".rowSpacer {height:2px;background-color:black;border:0px 0px 0px 0px'}");
        writer.print(".rowSpacerSmall {height:1px;background-color:white;border:0px 0px 0px 0px'}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold}");
        writer.print("table.errorDetail {}");
        writer.print(".param {background-color:#eee;font-weight:bolder}");
        writer.print(".paramStripe {}");
        writer.print(".stripe td {background-color: #FFF}");
        writer.print(".num {text-align:right}");
        writer.print(".passed td, .passed {background-color: #0F0}");
        writer.print(".skipped td, .skipped {background-color: #FF0}");
        writer.print(".failed td,.attn, .failed {background-color: #F00}");
        writer.print(".stacktrace {white-space:pre;font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:left;border-bottom:2px double #000}");
        writer.print(".methodNameCell {background-color:#ddd;}");
        writer.print(".resultNameCell {background-color:#ddd;}");
        writer.print(".startTimeCell {background-color:#ddd;}");
        writer.print(".durationCell {background-color:#ddd;}");        
        
        writer.print("</style>");
    }

    protected void writeBody() {
        writer.print("<body>");
        writeReportTitle( "Sahil Selenium TestNG Report" );
        writeSuiteSummary();
        writeScenarioSummary();
        writeScenarioDetails();
        writer.print("</body>");
    }

    protected void writeReportTitle( String title ) {
        writer.print( "<center><h1>" + title + " - " + getDateAsString() + "</h1></center>" );
    }

    protected void writeDocumentEnd() {
        writer.print("</html>");
    }

    protected void writeSuiteSummary() {

        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writer.print("<table>");
        writer.print("<tr>");
        writer.print("<th>Test</th>");
        writer.print("<th>Passed</th>");
        writer.print("<th>Skipped</th>");
        writer.print("<th>Failed</th>");
        writer.print("<th>Duration</th>");
        writer.print("<th>Included Groups</th>");
        writer.print("<th>Excluded Groups</th>");
        writer.print("</tr>");

        int testIndex = 0;
        for ( SuiteResult suiteResult : suiteResults ) {
            writer.print("<tr><th colspan='7' class='suiteHeader'>");
            writer.print( Utils.escapeHtml( suiteResult.getSuiteName() ) );
            writer.print("</th></tr>");

            for ( TestResult testResult : suiteResult.getTestResults() ) {
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();
                long duration = testResult.getDuration();

                writer.print("<tr");
                if ((testIndex % 2) == 1) {
                    writer.print(" class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData( buffer.append("<a href=\"#t").append(testIndex).append("\">").append(Utils.escapeHtml(testResult.getTestName())).append("</a>").toString());
                writeTableData( integerFormat.format(passedTests), "num");
                writeTableData( integerFormat.format(skippedTests), (skippedTests > 0 ? "skipped" : "num"));
                writeTableData( integerFormat.format(failedTests), (failedTests > 0 ? "num attn" : "num"));
                writeTableData( formatMilliseconds(duration), "num");
                writeTableData( testResult.getIncludedGroups() );
                writeTableData( testResult.getExcludedGroups() );

                writer.print("</tr>");

                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;

                testIndex++;
            }
        }

        // Print totals if there was more than one test
        if ( testIndex >= 1 ) {
        	writer.print("<tr><td colspan='7' style='height:10px;background-color:#aaa'>&nbsp;</td></tr>");
            writer.print("<tr>");
            writer.print("<th>Total</th>");
            writeTableHeader( integerFormat.format(totalPassedTests), "passed");
            writeTableHeader( integerFormat.format(totalSkippedTests), "skipped");
            writeTableHeader( integerFormat.format(totalFailedTests), "failed");
            writeTableHeader( formatMilliseconds(totalDuration), "num");
            writer.print("<th colspan=\"2\"></th>");
            writer.print("</tr>");
        }

        writer.print("</table>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {
    	writer.print("<div id='summary'></div>");
    	 writer.print("<table>");
        int testIndex = 0;
        int scenarioIndex = 0;
        for ( SuiteResult suiteResult : suiteResults ) {
            writer.print("<tbody><tr><th colspan='4' class='suiteHeader'>");
            writer.print( Utils.escapeHtml( suiteResult.getSuiteName() ) );
            writer.print("</th></tr></tbody>");

            for ( TestResult testResult : suiteResult.getTestResults() ) {
                writer.print("<tbody id='t" + testIndex + "'>");
                scenarioIndex += writeScenarioSummary(testResult, scenarioIndex);                
                writer.print("</tbody>");
                testIndex++;
            }
            writer.print("<tbody><tr><td colspan='4' class='rowSpacer'></td></tr></tbody>");
        }

        writer.print("</table>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(TestResult testResult, int startingScenarioIndex ) {
        int scenarioCount = 0;
        if (!testResult.allTestResults.isEmpty()) {
            writer.print("<tr><th colspan='4' class='testcaseHeader'>");
            writer.print(Utils.escapeHtml(testResult.getTestName()));
            writer.print("</th></tr>");            
            int scenarioIndex = startingScenarioIndex;
            int classIndex = 0;
            for ( ClassResult classResult : testResult.allTestResults ) {

            	String cssClass = "";
                buffer.setLength(0);	
                int scenariosPerClass = 0;
                int methodIndex = 0;
                for ( MethodResult methodResult : classResult.getMethodResults() ) {
                	List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    assert resultsCount > 0;

                    ITestResult itestResult = results.iterator().next();
                    cssClass = determineCssClass(itestResult);
                    
                    String methodName = Utils.escapeHtml(itestResult.getMethod().getMethodName());

                    long start = itestResult.getStartMillis();
                    long duration = itestResult.getEndMillis() - start;

                    buffer.append("<tr class=\"").append(cssClass).append("\">");

                    buffer.append("<td><a href=\"#m").append(scenarioIndex).append("\">").append( methodName + "</a></td>" )
                            .append("<td>" + itestResult.getName() + "</td>")
                            .append("<td>").append(parseUnixTimeToTimeOfDay(start)).append( "</td>")
                            .append("<td>").append(formatMilliseconds(duration)).append("</td></tr>");
                    
                    scenarioIndex++;

                    // Write the remaining scenarios for the method
                    
                    for ( int i = 1; i < resultsCount; i++ ) {
                        buffer.append("<tr class=\"").append(cssClass).append("\">")
                        	  .append("<td><a href=\"#m").append(scenarioIndex).append("\">").append( methodName + "</a></td>")
                              .append("<td rowspan=\"1\">" + itestResult.getName() + "</td></tr>");
                        scenarioIndex++;
                    }
                    

                    scenariosPerClass += resultsCount;
                    methodIndex++;
                }

               
                writer.print("<tr>");
                writer.print("<td colspan='4' class='testClassName'>");
                writer.print(Utils.escapeHtml(classResult.getClassName()).replace("com.avada.tools.selenium.", ""));
                writer.print("</td></tr>");
                writer.print("<thead>");
                writer.print("<tr>");
                writer.print("<th class='methodNameCell'>Method</th>");
                writer.print("<th class='resultNameCell'>Name</th>");
                writer.print("<th class='startTimeCell'>Start</th>");
                writer.print("<th class='durationCell'>Duration</th>");
                writer.print("</tr>");
                writer.print("</thead>");
                writer.print(buffer);
                
                classIndex++;
            }
            writer.print("<tr><td colspan='4' class='rowSpacerSmall'></td></tr>");
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }
    
    private String determineCssClass(ITestResult itestResult) {
    	switch(itestResult.getStatus()) {
    		case ITestResult.FAILURE: return "failed";
    		case ITestResult.SKIP: return "skipped";
    		case ITestResult.SUCCESS: return "passed";
    		default: return "unknown";
    	}
    }

    /**
     * Writes the details for all test scenarios.
     */
    protected void writeScenarioDetails() {
        int scenarioIndex = 0;
        for ( SuiteResult suiteResult : suiteResults ) {
            for ( TestResult testResult : suiteResult.getTestResults() ) {
                writer.print("<div style='font-size:24px;font-weight:bolder;background-color:#ccc;border:thin solid black' >");
                writer.print( Utils.escapeHtml(testResult.getTestName()) );
                writer.print("</div>");

                scenarioIndex += writeScenarioDetails(testResult.getAllTestResults(), scenarioIndex);
            }
        }
    }

    /**
     * Writes the scenario details for the results of a given state for a single
     * test.
     */
    private int writeScenarioDetails( List<ClassResult> classResults, int startingScenarioIndex ) {
        int scenarioIndex = startingScenarioIndex;
        for ( ClassResult classResult : classResults ) {
            String className = classResult.getClassName();
            for ( MethodResult methodResult : classResult.getMethodResults() ) {
                List<ITestResult> results = methodResult.getResults();
                assert !results.isEmpty();

                ITestResult mResult = results.iterator().next();
                String label = Utils.escapeHtml(mResult.getMethod().getMethodName().toUpperCase() + " ( " + mResult.getName() +" )" );
                for ( ITestResult result : results ) {
                    writeScenario( scenarioIndex, label, result );
                    scenarioIndex++;
                }
            }
        }
        writer.print("<div class='totop'><a href='#summary'>back to summary</a></div>");
        return scenarioIndex - startingScenarioIndex;
    }

    /**
     * Writes the details for an individual test scenario.
     */
    private void writeScenario(int scenarioIndex, String label, ITestResult result) {
    	Throwable throwable = result.getThrowable();
        if (throwable != null) {
	        writer.print("<div style='font-size:18px;font-weight:bolder;padding-top:5px;' id='m" + scenarioIndex + "'>");
	        writer.print(label.replace("com.avada.tools.selenium.testcases.",  ""));
	        writer.print("</div>");
	
	        writer.print("<table class='result'>");
	
            writer.print("<tr><td>");    
            writeStackTrace(throwable);
            writer.print("</td></tr>");
            writer.print("</table>");
        }
        
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        writer.print(Utils.escapeHtml(iterator.next()));
        while (iterator.hasNext()) {
            writer.print("<br/>");
            writer.print(Utils.escapeHtml(iterator.next()));
        }
        writer.print("</div>");
    }

    protected void writeStackTrace(Throwable throwable) {
    	String stackTrace = Utils.stackTrace(throwable, true)[0];
    	stackTrace = stackTrace.replace("java.lang.AssertionError: The following asserts failed:", "");
    	if(stackTrace.lastIndexOf("|") > 0)
    		stackTrace = stackTrace.substring(0, stackTrace.lastIndexOf("|")+1);
    	String output = "<table class='errorDetail'>";
    	String[] segments = stackTrace.split("\\|,");
    	if(segments.length > 0) {
     		for(int i=0; i<segments.length; i++) {
    			//String[] subSegments = segments[i].replace("|","").split("\\*xx");
     			String[] subSegments = segments[i].split("\\*xx");     			
     			if(subSegments.length == 2) {
     				String[] superSubSegments = subSegments[1].split("\\|");
     				if(superSubSegments.length == 2) {
     	   				output += "<tr><td style='width:40px'><a href='superSubSegments[1]' target='_blank'>[IMG]</a></td><td>"+superSubSegments[0]+"</td><td>"+subSegments[0]+"</td></tr>";
     				}
     				if(superSubSegments.length == 1) {
     					output += "<tr><td>"+superSubSegments[0]+"</td><td>&nbsp;</td><td>"+subSegments[0]+"</td></tr>";
     				}
     			}else{
     				output += "<tr><td colspan='2'>"+segments[i]+"</td></tr>";
     			}		
    		}
    	}
    	output += "</table>";
        writer.print(output);
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     *
     * @param tag        the tag name
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"");
            writer.print(cssClasses);
            writer.print("\"");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();
        private final TreeSet<Date> endDates = new TreeSet<Date>();
        private Date endDate = null;
        
        protected static final Comparator<SuiteResult> SUITE_COMPARATOR = new Comparator<SuiteResult>() {
            public int compare(SuiteResult o1, SuiteResult o2) {
            	if(o1.getEndDate() == null)
            		return -1;
            	if(o2.getEndDate() == null)
            		return 1;
            	return o1.getEndDate().compareTo(o2.getEndDate());
            }
        };
      

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
            	ITestContext testContext = suiteResult.getTestContext();
                testResults.add(new TestResult(testContext));
                endDates.add(testContext.getEndDate());
            }
             if(endDates.size() > 0) {
            	Date d = endDates.last();
                if(d != null)
                	endDate = endDates.last();
            }  
             
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }

		public Date getEndDate() {
			return endDate;
		}

		        
        
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {  	
        	public int compare(ITestResult o1, ITestResult o2) {            	
                return ((Long)o1.getEndMillis()).compareTo((Long)o2.getEndMillis());
            }
        };

        private final String testName;
        private final List<ClassResult> allTestResults = new ArrayList<ClassResult>();
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();
            duration = context.getEndDate().getTime() - context.getStartDate().getTime();
  
            
            Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests().getAllResults(); 
            
            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
          
            Set<ITestResult> allResults = new HashSet<ITestResult>();
            allResults.addAll(failedConfigurations);
            allResults.addAll(failedTests);
            allResults.addAll(skippedConfigurations);
            allResults.addAll(skippedTests);
            allResults.addAll(passedTests);
            
            groupResults(allResults);       
  
            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

        /**
         * Groups test results by method and then by class.
         */
        protected void groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList, RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName, resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName, resultsPerClass));
            }
            allTestResults.addAll(classResults);
        }

        public String getTestName() {
            return testName;
        }

        
        /**
         * @return combine ALL tests (possibly empty)
         */
        public List<ClassResult> getAllTestResults() {
			return allTestResults;
		}

		public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }

        /**
         * Formats an array of groups for display.
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className     the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;

        /**
         * @param results the non-null, non-empty result list
         */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }
    }

    /*
    Methods to improve time display on reports
     */
    protected String getDateAsString() {
        Date date = new Date();
        sdfdate.setTimeZone( TimeZone.getTimeZone( timeZone ) );
        return sdfdate.format( date );
    }

    protected String parseUnixTimeToTimeOfDay( long unixSeconds ) {
        Date date = new Date( unixSeconds );
        sdftime.setTimeZone( TimeZone.getTimeZone( timeZone ) );
        return sdftime.format( date );
    }

    protected double millisecondsToSeconds( long ms ) {
        return new BigDecimal( ms/1000.00 ).setScale( 2, RoundingMode.HALF_UP ).doubleValue();
    }
    
    protected String formatMilliseconds(long ms) {
    	return DurationFormatUtils.formatDuration(ms, "");
    }

}