package score.layout;

import static util.CollectionUtil.map;
import static util.CollectionUtil.max;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import score.VoiceItem;
import score.KeySig;
import score.MeasureAccidentals;
import score.Voice;

/**
 * Lays out items across multiple voices so that items that occur together are aligned by adding extra
 * spacers as needed.
 */
public class ItemLayout {
	private static class VoiceAndItem {
		private final Voice voice;
		private final VoiceItem item;
		private final AlignmentBox alignmentBox;
		
		public VoiceAndItem(Voice voice, VoiceItem item, AlignmentBox alignmentBox) {
			this.voice = voice;
			this.item = item;
			this.alignmentBox = alignmentBox;
		}
	}
	
	private final Map<Voice, List<VoiceItem>> spacedItems = new LinkedHashMap<>();
	
	public ItemLayout(KeySig keySig, List<Voice> voices, int extraSpacing) {
		Map<Voice, Integer> voiceWidths = new LinkedHashMap<>();
		voices.forEach(line -> voiceWidths.put(line, 0));
		
		// Find the items that begin at each time offset.
		Map<Integer, List<VoiceAndItem>> itemsAtCount = new TreeMap<>();
		for(Voice voice:voices) {
			int count = 0;
			MeasureAccidentals measureAccidentals = new MeasureAccidentals(keySig);
			for(VoiceItem item:voice.getItems()) {
				if(item.includeInLayout()) {
					itemsAtCount.computeIfAbsent(count, c -> new ArrayList<>());
					AlignmentBox alignmentBox = item.getAlignmentBox(measureAccidentals);
					item.setAccidentals(measureAccidentals);
					itemsAtCount.get(count).add(new VoiceAndItem(voice, item, alignmentBox));
					count += item.getDurationCount();
				}
			}
		}
		
		Divider spacingDivider = new Divider(extraSpacing, itemsAtCount.size() + 1);
		
		int minSpacing = 60;
		
		// Add items and spacers to spacedItems to line up items at same time offset.
		itemsAtCount.forEach((count, voiceAndItems) -> {
			int center = max(map(voiceAndItems, voiceAndItem -> {
				int voiceWidth = voiceWidths.get(voiceAndItem.voice);
				int itemCenter = voiceAndItem.alignmentBox.getCenter();
				
				return voiceWidth + itemCenter + (voiceWidth == 0 ? 0 : minSpacing);
			}));
			
			int currentExtraSpacing = spacingDivider.next();
			
			voiceAndItems.forEach(voiceAndItem -> {
				int lineWidth = voiceWidths.get(voiceAndItem.voice);
				int itemWidth = voiceAndItem.alignmentBox.getWidth();
				int itemCenter = voiceAndItem.alignmentBox.getCenter();
				
				int spacing = center - (lineWidth + itemCenter) + currentExtraSpacing;
				
				voiceWidths.put(voiceAndItem.voice, lineWidth + spacing + itemWidth);
				
				spacedItems.computeIfAbsent(voiceAndItem.voice, line -> new ArrayList<>());
				if(spacing > 0) {
					spacedItems.get(voiceAndItem.voice).add(new Spacer(spacing));
				}
				spacedItems.get(voiceAndItem.voice).add(voiceAndItem.item);
			});
		});
		
		// Add final spacing at the end of each voice.
		spacedItems.forEach((voice, items) -> {
			items.add(new Spacer(minSpacing));
		});
	}

	/**
	 * Returns the items for each voice with the extra spacers added.
	 */
	public Map<Voice, List<VoiceItem>> getVoiceItems() {
		return spacedItems;
	}
}