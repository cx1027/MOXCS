package nxcs.stats;

public class StepStatsPoint{
	public int timestamp;
	public double matchedRate;
	public double coverage;
	public StepStatsPoint(int ts, double matchRate, double coverage)
	{
		this.timestamp =ts;
		this.matchedRate = matchRate;
		this.coverage = coverage;
	}

	@Override
	public String toString(){
		return String.format("t:%d\tM:%3.2f\tC:%3.2f",this.timestamp, this.matchedRate, this.coverage);
	}
	
	public String toCSV() {
		StringBuilder build = new StringBuilder();
		build.append(timestamp);
		build.append(", ");
		build.append(matchedRate);
		build.append(", ");
		build.append(coverage);
		build.append("\n");

		return build.toString();
	}
}