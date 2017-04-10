package am.ik.blog.entry.criteria;

import am.ik.blog.entry.Name;
import am.ik.blog.entry.Tag;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SearchCriteria {
	public static final SearchCriteria DEFAULT = defaults().build();

	private boolean excludeContent;
	private Name createdBy;
	private Name lastModifiedBy;
	private Tag tag;
	private CategoryOrders categoryOrders;
	private String keyword;

	public static SearchCriteria.SearchCriteriaBuilder defaults() {
		return SearchCriteria.builder().excludeContent(true);
	}

}
