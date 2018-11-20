package score;

import static util.CollectionUtil.map;
import static util.CollectionUtil.max;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NoteLayout {
	private static class VoiceAndItem {
		private final Voice voice;
		private final CanvasItem item;
		
		public VoiceAndItem(Voice voice, CanvasItem item) {
			this.voice = voice;
			this.item = item;
		}
	}
	
	private final Map<Voice, List<CanvasItem>> spacedItems = new LinkedHashMap<>();
	
	public NoteLayout(List<Voice> voices, int extraSpacing) {
		Map<Voice, Integer> voiceWidths = new LinkedHashMap<>();
		voices.forEach(line -> voiceWidths.put(line, 0));
		
		Map<Integer, List<VoiceAndItem>> itemsAtCount = new TreeMap<>();
		for(Voice voice:voices) {
			int count = 0;
			for(CanvasItem item:voice.getItems()) {
				itemsAtCount.computeIfAbsent(count, c -> new ArrayList<>());
				itemsAtCount.get(count).add(new VoiceAndItem(voice, item));
				count += item.getDuration();
			}
		}
		
		Divider spacingDivider = new Divider(extraSpacing, itemsAtCount.size() + 1);
		
		int minSpacing = 60;
		
		itemsAtCount.forEach((count, voiceAndItems) -> {
			int center = max(map(voiceAndItems, voiceAndItem -> {
				int voiceWidth = voiceWidths.get(voiceAndItem.voice);
				int itemCenter = voiceAndItem.item.getAlignmentBox().getCenter();
				
				return voiceWidth + itemCenter + (voiceWidth == 0 ? 0 : minSpacing);
			}));
			
			int currentExtraSpacing = spacingDivider.next();
			
			voiceAndItems.forEach(voiceAndItem -> {
				int lineWidth = voiceWidths.get(voiceAndItem.voice);
				int itemWidth = voiceAndItem.item.getAlignmentBox().getWidth();
				int itemCenter = voiceAndItem.item.getAlignmentBox().getCenter();
				
				int spacing = center - (lineWidth + itemCenter) + currentExtraSpacing;
				
				voiceWidths.put(voiceAndItem.voice, lineWidth + spacing + itemWidth);
				
				spacedItems.computeIfAbsent(voiceAndItem.voice, line -> new ArrayList<>());
				if(spacing > 0) {
					spacedItems.get(voiceAndItem.voice).add(new Spacer(spacing));
				}
				spacedItems.get(voiceAndItem.voice).add(voiceAndItem.item);
			});
		});
		
		spacedItems.forEach((voice, items) -> {
			items.add(new Spacer(minSpacing));
		});
	}

	public Map<Voice, List<CanvasItem>> getVoiceItems() {
		return spacedItems;
	}
}