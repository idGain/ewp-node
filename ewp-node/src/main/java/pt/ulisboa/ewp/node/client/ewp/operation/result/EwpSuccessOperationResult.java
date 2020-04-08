package pt.ulisboa.ewp.node.client.ewp.operation.result;

public class EwpSuccessOperationResult<T> extends AbstractEwpOperationResult {

  private T responseBody;

  protected EwpSuccessOperationResult(Builder<T> builder) {
    super(EwpOperationResultType.SUCCESS, builder);
    this.responseBody = builder.responseBody;
  };

  public T getResponseBody() {
    return responseBody;
  }

  public static class Builder<T> extends AbstractEwpOperationResult.Builder<Builder<T>> {

    private T responseBody;

    public T responseBody() {
      return responseBody;
    }

    public Builder<T> responseBody(T responseBody) {
      this.responseBody = responseBody;
      return this;
    }

    @Override
    public Builder<T> getThis() {
      return this;
    }

    public EwpSuccessOperationResult<T> build() {
      return new EwpSuccessOperationResult<T>(this);
    }
  }
}
