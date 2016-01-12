package nxcs.stats;

public class StepStatsPoint{
	public int timeStamp;
	public double matchRate;
	public double coverage;
	public StepStatsPoint(int ts, double matchRate, double coverage)
	{
		this.timeStamp =ts;
		this.matchRate = matchRate;
		this.coverage = coverage;
	}		
}