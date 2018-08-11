package am.ik.blog.entry.criteria;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import am.ik.blog.entry.Name;
import am.ik.blog.entry.Tag;
import lombok.Builder;
import lombok.Getter;

import org.springframework.util.StringUtils;

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

	public String toJoinClause() {
		StringBuilder sb = new StringBuilder();
		if (this.tag != null) {
			sb.append("LEFT JOIN entry_tag AS et ON e.entry_id = et.entry_id ");
		}
		if (this.categoryOrders != null) {
			sb.append("LEFT JOIN category AS c ON e.entry_id = c.entry_id ");
		}
		return sb.toString();
	}

	public ClauseAndParams toWhereClause() {
		Map<String, String> clause = new LinkedHashMap<>();
		Map<String, Object> params = new HashMap<>();
		if (!StringUtils.isEmpty(this.keyword)) {
			params.put("keyword", "%" + this.keyword + "%");
			clause.put("keyword", "AND e.content LIKE :keyword");
		}
		if (this.createdBy != null) {
			params.put("created_by", this.createdBy.getValue());
			clause.put("created_by", "AND e.created_by = :created_by");
		}
		if (this.lastModifiedBy != null) {
			params.put("last_modified_by", this.lastModifiedBy.getValue());
			clause.put("last_modified_by", "AND e.last_modified_by = :last_modified_by");
		}
		if (this.categoryOrders != null) {
			this.categoryOrders.getValue().forEach(c -> {
				int categoryOrder = c.getCategoryOrder();
				String categoryNameKey = "category_name" + categoryOrder;
				String categoryOrderKey = "category_order" + categoryOrder;
				params.put(categoryNameKey, c.getCategory().getValue());
				clause.put(categoryNameKey, "AND c.category_name = :" + categoryNameKey);
				params.put(categoryOrderKey, categoryOrder);
				clause.put(categoryOrderKey,
						"AND c.category_order = :" + categoryOrderKey);
			});
		}
		if (this.tag != null) {
			params.put("tag", this.tag.getValue());
			clause.put("tag", "AND et.tag_name = :tag");
		}
		return new ClauseAndParams(clause, params);
	}

	public static class ClauseAndParams {
		private final Map<String, String> clause;
		private final Map<String, Object> params;

		ClauseAndParams(Map<String, String> clause, Map<String, Object> params) {
			this.clause = clause;
			this.params = params;
		}

		public String clauseForEntryId() {
			return this.clause.values().stream().collect(Collectors.joining(" "));
		}

		public Map<String, Object> params() {
			return this.params;
		}
	}
}
