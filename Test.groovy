class Test extends GroovyTestCase {

    Test() {
        println "I live!"
    }

    void testIvy() {
        def report = new org.apache.ivy.Ivy()
        def ivyHome = report.getIvyHomeURL()
        assertTrue(ivyHome.contains("apache.org"))
    }

    void testCLI() {
        def cli = new CliBuilder(usage: 'Usage')
        def expected = 'Usage'
        assertToString(cli.usage, expected)
    }
}
