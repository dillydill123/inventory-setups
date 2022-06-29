package inventorysetups.ui;

public interface InventorySetupsMoveHandler<T>
{
    void moveUp(final T datum);
    void moveDown(final T datum);
    void moveToTop(final T datum);
    void moveToBottom(final T datum);
    void moveToPosition(final T datum);
}
