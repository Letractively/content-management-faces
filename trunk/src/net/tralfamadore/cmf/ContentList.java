/*
 * Copyright (c) 2011 Bill Reh.
 *
 * This file is part of Content Management Faces.
 *
 * Content Management Faces is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package net.tralfamadore.cmf;

import java.util.List;
import java.util.Vector;

/**
 * User: billreh
 * Date: 1/19/11
 * Time: 12:15 AM
 */
public class ContentList extends BaseContent {
    private Content contentDivider;
    private List<Content> contentList = new Vector<Content>();
    private int numberOfItemsToShow = 5;


    public void addContent(Content content) {
        contentList.add(0, content);
    }

    public Content getContentDivider() {
        return contentDivider;
    }

    public void setContentDivider(Content contentDivider) {
        this.contentDivider = contentDivider;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public List<Content> getItemsToShow() {
        if(contentList.size() <= numberOfItemsToShow)
            return contentList;

        return contentList.subList(0, numberOfItemsToShow);
    }

    public int getNumberOfItemsToShow() {
        return numberOfItemsToShow;
    }

    public void setNumberOfItemsToShow(int numberOfItemsToShow) {
        this.numberOfItemsToShow = numberOfItemsToShow;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentList)) return false;
        if (!super.equals(o)) return false;

        ContentList that = (ContentList) o;

        if (numberOfItemsToShow != that.numberOfItemsToShow) return false;
        if (contentDivider != null ? !contentDivider.equals(that.contentDivider) : that.contentDivider != null)
            return false;
        //noinspection RedundantIfStatement
        if (contentList != null ? !contentList.equals(that.contentList) : that.contentList != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (contentDivider != null ? contentDivider.hashCode() : 0);
        result = 31 * result + (contentList != null ? contentList.hashCode() : 0);
        result = 31 * result + numberOfItemsToShow;
        return result;
    }
}
