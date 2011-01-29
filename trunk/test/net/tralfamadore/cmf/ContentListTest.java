package net.tralfamadore.cmf;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 2:16 AM
 */
public class ContentListTest {
    @Test
    public void testContentList() {
        int i = 0;
        ContentList contentList = new ContentList();

        // add three content items
        for( ; i < 3; i++)
            contentList.addContent(new Content());

        // make sure it's all there
        assertEquals(3, contentList.getContentList().size());
        assertEquals(3, contentList.getItemsToShow().size());

        // add three more
        for( ; i < 6; i++)
            contentList.addContent(new Content());

        // itemsToShow should only be 5 (default) entries, contentList should be 6
        assertEquals(5, contentList.getItemsToShow().size());
        assertEquals(6, contentList.getContentList().size());

        // make sure we can change default items to show
        contentList.setNumberOfItemsToShow(3);
        assertEquals(3, contentList.getItemsToShow().size());

        // Add another
        Content content = new Content();
        contentList.addContent(content);

        // Content should be added to the beginning of the list
        assertEquals(content, contentList.getContentList().get(0));
        assertNotSame(content, contentList.getContentList().get(1));
    }
}
