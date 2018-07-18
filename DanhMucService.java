package vn.toancauxanh.cms.service;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.TreeNode;

import com.querydsl.jpa.impl.JPAQuery;

import vn.toancauxanh.gg.model.DanhMuc;
import vn.toancauxanh.gg.model.QDanhMuc;
import vn.toancauxanh.service.BasicService;

public class DanhMucService extends BasicService<DanhMuc> {

	private String img = "/backend/assets/img/edit.png";
	private String hoverImg = "/backend/assets/img/edit_hover.png";
	private String strUpdate = "Thứ tự";
	private boolean update = true;
	private boolean updateThanhCong = true;

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getHoverImg() {
		return hoverImg;
	}

	public void setHoverImg(String hoverImg) {
		this.hoverImg = hoverImg;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean isUpdateThanhCong() {
		return updateThanhCong;
	}

	public void setUpdateThanhCong(boolean updateThanhCong) {
		this.updateThanhCong = updateThanhCong;
	}

	public void openObject(DefaultTreeModel<DanhMuc> model, TreeNode<DanhMuc> node) {
		if (node.isLeaf()) {
			model.addOpenObject(node);
		} else {
			for (TreeNode<DanhMuc> child : node.getChildren()) {
				model.addOpenObject(child);
				openObject(node.getModel(), child);
			}
		}
	}

	public DefaultTreeModel<DanhMuc> getModel() {
		DanhMuc danhMucGoc = new DanhMuc();
		DefaultTreeModel<DanhMuc> model = new DefaultTreeModel<DanhMuc>(danhMucGoc.getNode(), true);
		for (DanhMuc danhMuc : getList()) {
			if (danhMuc.loadSizeChild() > 0) {
				danhMucGoc.getNode().add(danhMuc.getNode());
			}
		}
		openObject(model, danhMucGoc.getNode());
		BindUtils.postNotifyChange(null, null, this, "sizeOfCategories");
		return model;
	}

	/*public List<DanhMuc> getList() {
		JPAQuery<DanhMuc> q = find(DanhMuc.class);

		// truy vấn lấy ra danh mục cha và sắp xếp tăng dần
		q.where(QDanhMuc.danhMuc.trangThai.ne(core().TT_DA_XOA)).where(QDanhMuc.danhMuc.parent.isNull());
		q.orderBy(QDanhMuc.danhMuc.soThuTu.asc());
		List<DanhMuc> list = q.fetch();
		for (DanhMuc danhMuc : list) {
			danhMuc.loadChildren();
		}
		return list;
	}*/
	
	public List<DanhMuc> getList() {
		JPAQuery<DanhMuc> q = find(DanhMuc.class);
		q.where(QDanhMuc.danhMuc.trangThai.ne(core().TT_DA_XOA))
			.where(QDanhMuc.danhMuc.parent.isNull());
		q.orderBy(QDanhMuc.danhMuc.soThuTu.asc());
		List<DanhMuc> list = q.fetch();
		for (DanhMuc danhMuc : list) {
			System.out.println(danhMuc.getName());
		}
		for (DanhMuc danhMuc : list) {
			danhMuc.loadChildren();
		}
		return list;
	}

	public List<DanhMuc> getDanhMucCon(DanhMuc danhMuc) {
		List<DanhMuc> list = new ArrayList<>();
		if (danhMuc.getTrangThai().equalsIgnoreCase(core().TT_DA_XOA)) {
			for (TreeNode<DanhMuc> el : danhMuc.getNode().getChildren()) {
				list.add(el.getData());
				list.addAll(getDanhMucCon(el.getData()));
			}
		}
		return list;
	}

	// dùng để kiểm tra danh sách chủ đề
	// có rỗng không
	public long getSizeOfCategories() {
		JPAQuery<DanhMuc> q = find(DanhMuc.class).where(QDanhMuc.danhMuc.trangThai.ne(core().TT_DA_XOA));
		return q.fetchCount();
	}

	// =========================================

	public List<DanhMuc> getListAllCategoryAndNullButThis(DanhMuc self) {

		// nếu là thêm mới thì self bằng null
		List<DanhMuc> list = new ArrayList<>();
		list.add(null);
		for (DanhMuc cat : getListAllButThis(self)) {
			list.add(cat);
			list.addAll(getCategoryChildrenButThis(cat, self));
		}
		return list;
	}

	public List<DanhMuc> getListAllButThis(DanhMuc self) {
		JPAQuery<DanhMuc> q = find(DanhMuc.class);
		q.where(QDanhMuc.danhMuc.trangThai.ne(core().TT_DA_XOA)).where(QDanhMuc.danhMuc.parent.isNull());
		q.orderBy(QDanhMuc.danhMuc.soThuTu.asc());

		// không lấy lại chính nó (dùng khi chỉnh sửa)
		if (self != null && !self.noId()) {
			q.where(QDanhMuc.danhMuc.id.ne(self.getId()));
		}

		List<DanhMuc> list = q.fetch();

		for (DanhMuc danhMuc : list) {
			danhMuc.loadChildren();
		}
		return list;
	}

	// bỏ qua, không lấy thằng cha của nó
	public List<DanhMuc> getCategoryChildrenButThis(DanhMuc danhMuc, DanhMuc ignore) {
		List<DanhMuc> list = new ArrayList<>();
		if (!danhMuc.getTrangThai().equals(core().TT_DA_XOA)) {
			for (TreeNode<DanhMuc> el : danhMuc.getNode().getChildren()) {
				if (ignore.getId() != el.getData().getId()) {
					list.add(el.getData());
					list.addAll(getCategoryChildrenButThis(el.getData(), ignore));
				}
			}
		}
		return list;
	}
}
