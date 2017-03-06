package TimeSeriesLabelingTools;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import com.github.TKnudsen.ComplexDataObject.data.ranking.Ranking;
import com.github.TKnudsen.timeseries.data.ITemporalLabeling;
import com.github.TKnudsen.timeseries.data.ITimeSeries;
import com.github.TKnudsen.timeseries.data.primitives.TimeDuration;
import com.github.TKnudsen.timeseries.data.primitives.TimeIntervalLabel;
import com.github.TKnudsen.timeseries.data.primitives.TimeQuantization;

public class TimeSeriesLabelingTools {

	public static <O extends Object> O getLabel(ITemporalLabeling<O> timeSeries, long timeStamp) {
		if (timeSeries == null)
			return null;

		SortedMap<Long, O> labelChangeEvents = getLabelChangeEvents(timeSeries);

		@SuppressWarnings("unchecked")
		O label = (O) "noLabel";

		for (Long l : labelChangeEvents.keySet()) {
			if (l > timeStamp)
				return label;
			else
				label = labelChangeEvents.get(l);
		}

		return null;
	}

	/**
	 * retrieves events of changing labels for a ITemporalLabeling data
	 * structure
	 * 
	 * @param timeSeries
	 * @return
	 */
	public static <O extends Object> SortedMap<Long, O> getLabelChangeEvents(ITemporalLabeling<O> timeSeries) {
		if (timeSeries == null)
			return null;

		ITimeSeries<?> ts = null;
		if (timeSeries instanceof ITimeSeries)
			ts = (ITimeSeries<?>) timeSeries;

		Ranking<TimeIntervalLabel<O>> intervalLabels = timeSeries.getIntervalLabels();
		SortedMap<Long, O> eventLabels = timeSeries.getEventLabels();

		if (ts == null)
			return getLabelChangeEvents(intervalLabels, eventLabels, new TimeDuration(TimeQuantization.MILLISECONDS, 1));

		SortedMap<Long, O> sortedEvents = new TreeMap<Long, O>();

		SortedMap<Long, O> labelProgressionOverTime = getLabelProgressionOverTime(timeSeries);
		sortedEvents.put(labelProgressionOverTime.firstKey(), labelProgressionOverTime.get(labelProgressionOverTime.firstKey()));
		O labelTmp = sortedEvents.get(labelProgressionOverTime.firstKey());
		for (Long l : labelProgressionOverTime.keySet())
			if (labelProgressionOverTime.get(l) != labelTmp) {
				sortedEvents.put(l, labelProgressionOverTime.get(l));
				labelTmp = labelProgressionOverTime.get(l);
			}

		return sortedEvents;
	}

	/**
	 * pure label signature without temporal information of a (e.g.) time
	 * series.
	 * 
	 * @param intervalLabels
	 * @param eventLabels
	 * @return
	 */
	public static <O extends Object> SortedMap<Long, O> getLabelChangeEvents(Ranking<TimeIntervalLabel<O>> intervalLabels, SortedMap<Long, O> eventLabels, TimeDuration timeDuration) {

		SortedMap<Long, O> sortedEvents = new TreeMap<Long, O>();

		// add intervals
		for (TimeIntervalLabel<O> timeIntervalLabel : intervalLabels)
			sortedEvents.put(timeIntervalLabel.getStartTime(), timeIntervalLabel.getLabel());

		// add events (tricky)
		Set<Long> k = sortedEvents.keySet();
		Set<Long> keySet = new TreeSet<>(k); // avoid concurrent
		Iterator<Long> iterator = keySet.iterator();
		Long l = Long.MIN_VALUE + 1;
		if (iterator.hasNext())
			l = iterator.next();

		for (Long e : eventLabels.keySet()) {
			// add event
			sortedEvents.put(e, eventLabels.get(e));

			// determine next time stamp after event
			@SuppressWarnings("unchecked")
			O nextLabel = (O) "noLabel";
			while (l <= e) {
				if (l != Long.MIN_VALUE + 1)
					nextLabel = sortedEvents.get(l);
				if (iterator.hasNext())
					l = iterator.next();
				else
					break;
			}

			if (l > e + timeDuration.getDuration())
				l = e + timeDuration.getDuration();

			// add next time stamp event
			sortedEvents.put(l, nextLabel);
		}

		return sortedEvents;
	}

	@SuppressWarnings("unchecked")
	public static <O extends Object> SortedMap<Long, O> getLabelProgressionOverTime(ITemporalLabeling<O> timeSeries) {
		if (timeSeries == null)
			return null;

		ITimeSeries<?> ts = null;
		if (timeSeries instanceof ITimeSeries)
			ts = (ITimeSeries<?>) timeSeries;

		// (1) initialize label output time series
		SortedMap<Long, O> sortedEvents = new TreeMap<Long, O>();
		if (ts != null)
			for (Long l : ts.getTimestamps())
				sortedEvents.put(l, (O) "noLabel");

		// (2) add intervals in the order of their first time stamp
		Ranking<TimeIntervalLabel<O>> intervalLabels = timeSeries.getIntervalLabels();
		for (TimeIntervalLabel<O> timeIntervalLabel : intervalLabels) {
			Long l = timeIntervalLabel.getStartTime();
			int index = -1;
			try {
				index = ts.findByDate(l, true);
			} catch (Exception e) {
				for (Long timeStamp : ts.getTimestamps())
					if (timeStamp < l)
						continue;
					else {
						if (timeStamp <= ts.getLastTimestamp())
							index = ts.findByDate(timeStamp, true);
						break;
					}
			}

			if (index != -1) {
				while (true) {
					l = ts.getTimestamp(index);
					if (l > timeIntervalLabel.getEndTime())
						break;
					sortedEvents.put(l, timeIntervalLabel.getLabel());
					index++;
				}
			}
		}

		// (3) add events
		SortedMap<Long, O> eventLabels = timeSeries.getEventLabels();
		for (Long e : eventLabels.keySet()) {
			sortedEvents.put(e, eventLabels.get(e));
		}

		return sortedEvents;
	}
}
