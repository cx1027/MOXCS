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
}