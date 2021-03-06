package de.unistuttgart.vis.vita.importer.epub.extractors;

import java.util.List;

import nl.siegmann.epublib.domain.Resource;
import de.unistuttgart.vis.vita.importer.util.ChapterPosition;

/**
 * Calculates the ChapterPostion for part or parts regarding epub2 and epub3 
 * 
 *
 */
public class ChapterPositionMaker {

  private Epub2TraitsExtractor epublineTraitsExtractor;

  public ChapterPosition calculateChapterPositionsEpub2(List<List<Epubline>> newChapters,
      List<Resource> resources, Resource tocResource) {

    epublineTraitsExtractor = new Epub2TraitsExtractor(resources, tocResource);
    int currentSize = 0;
    ChapterPosition chapterPosition = new ChapterPosition();
    for (List<Epubline> chapter : newChapters) {

      chapterPosition
          .addChapter(chapter.indexOf(epublineTraitsExtractor.getHeading(chapter)) + currentSize,
              chapter.indexOf(epublineTraitsExtractor.getTextStart(chapter)) + currentSize,
              chapter.indexOf(epublineTraitsExtractor.getTextEnd(chapter)) + currentSize);

      currentSize = currentSize + chapter.size();
    }
    return chapterPosition;
  }

  public ChapterPosition calculateChapterPositionsEpub3(List<List<Epubline>> newChapters) {

    int currentSize = 0;
    ChapterPosition chapterPosition = new ChapterPosition();
    for (List<Epubline> chapter : newChapters) {
     
        chapterPosition.addChapter(chapter.indexOf(chapter.get(0)) + currentSize,
            chapter.indexOf(chapter.get(2)) + currentSize,
            chapter.indexOf(chapter.get(chapter.size() - 2)) + currentSize);
        currentSize = currentSize + chapter.size();
    }
    return chapterPosition;

  }
}
