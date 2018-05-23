package cn.lt.game.statistics;

public class NodeObject {

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public Object getId() {
		return id;
	}

	public void setId(Object id) {
		this.id = id;
	}

	private String node;

	private Object id;

	public NodeObject(Object id, String node) {
		this.id = id;
		this.node = node;
	}

	@Override
	public boolean equals(Object o) {

		try {
			NodeObject node = (NodeObject) o;
			if (o != null && this.id == node.getId()
					&& node.getNode().equals(this.node)) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
