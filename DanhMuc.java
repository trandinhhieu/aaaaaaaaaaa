package vn.toancauxanh.gg.model;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.TreeNode;

import com.google.common.base.Strings;

import vn.toancauxanh.model.Model;

@Entity
@Table(name = "danhmuc")
public class DanhMuc extends Model<DanhMuc> {

	private String name = "";
	private String description = "";
	private DanhMuc parent;
	private String alias = "";
	private int soThuTu;

	public DanhMuc() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = Strings.nullToEmpty(name);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = Strings.nullToEmpty(description);
	}

	@ManyToOne
	public DanhMuc getParent() {
		return parent;
	}

	public void setParent(DanhMuc parent) {
		this.parent = parent;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = Strings.nullToEmpty(alias);
	}

	public int getSoThuTu() {
		return soThuTu;
	}

	public void setSoThuTu(int soThuTu) {
		this.soThuTu = soThuTu;
	}

	private transient final TreeNode<DanhMuc> node = new DefaultTreeNode<DanhMuc>(this,
			new ArrayList<DefaultTreeNode<DanhMuc>>());

	@Transient
	public TreeNode<DanhMuc> getNode() {
		return node;
	}

	@Transient
	public String getChildName() {
		int count = 0;
		String s = " ";
		for (DanhMuc cha = getParent(); cha != null; cha = cha.getParent())
			count++;
		for (int i = 0; i <= count; i++)
			s += " - ";
		return s + this.name;
	}

	public void loadChildren() {
		for (final DanhMuc con : find(DanhMuc.class).where(QDanhMuc.danhMuc.parent.eq(this))
				.where(QDanhMuc.danhMuc.trangThai.ne(core().TT_DA_XOA)).orderBy(QDanhMuc.danhMuc.soThuTu.asc())
				.fetch()) {
			con.loadChildren();
			node.add(con.getNode()); // danh sách treenode<DanhMuc>
		}
	}

	public int loadSizeChild() {
		int size = core().getDanhMuc().getDanhMucCon(this).size();
		return size;
	}
}