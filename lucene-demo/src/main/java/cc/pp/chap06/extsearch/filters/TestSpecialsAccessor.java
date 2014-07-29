package cc.pp.chap06.extsearch.filters;

public class TestSpecialsAccessor implements SpecialsAccessor {

	private final String[] isbns;

	public TestSpecialsAccessor(String[] isbns) {
		this.isbns = isbns;
	}

	@Override
	public String[] isbns() {
		return isbns;
	}

}
