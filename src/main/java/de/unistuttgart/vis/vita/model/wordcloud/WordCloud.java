package de.unistuttgart.vis.vita.model.wordcloud;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.unistuttgart.vis.vita.model.entity.AbstractEntityBase;

/**
 * Represents the model for a WordCloud. A WordCloud shows a set of words in different font sizes,
 * depending on how often they are used in a document.
 */
@Entity
@XmlRootElement
public class WordCloud extends AbstractEntityBase {
  @OneToMany(cascade = CascadeType.ALL)
  @XmlElement
  @OrderBy("frequency DESC")
  private Set<WordCloudItem> items;

  /**
   * Creates a new empty WordCloud.
   */
  public WordCloud() {
    // higher items (with higher scores) should be at the top
    this.items = new TreeSet<WordCloudItem>().descendingSet();
  }

  /**
   * Creates a new WordCloud with the given items.
   *
   * @param pItems - the items for the new WordCloud
   */
  public WordCloud(Collection<WordCloudItem> pItems) {
    this();
    items.addAll(pItems);
  }

  /**
   * @return a Set of all items of this WordCloud
   */
  public Set<WordCloudItem> getItems() {
    return items;
  }

  /**
   * Sets the items for this WordCloud
   */
  public void setItems(SortedSet<WordCloudItem> newItems) {
    this.items = newItems;
  }

}
