import React, { useState } from "react";
import { authHttp } from "../../api/client";
import { getToken } from "../../state/authStorage";
import { useAuth } from "../../state/auth";
import { Button } from "../../ui/components/Button";
import { Card, CardContent, CardHeader, CardTitle } from "../../ui/components/Card";
import { Input } from "../../ui/components/Input";

export function AccountSecurity({
  title,
  description,
  children
}: {
  title: string;
  description: string;
  children?: React.ReactNode;
}) {
  const { user } = useAuth();
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [deleteText, setDeleteText] = useState("");
  const [deleteNote, setDeleteNote] = useState<string | null>(null);

  const changePassword = async () => {
    setMessage(null);
    setError(null);
    if (!user?.id) {
      setError("This session has no user id.");
      return;
    }
    if (newPassword.length < 8) {
      setError("New password must be at least 8 characters.");
      return;
    }
    if (newPassword !== confirm) {
      setError("New password and confirmation do not match.");
      return;
    }
    setBusy(true);
    try {
      const token = getToken();
      const refresh = window.localStorage.getItem("refreshToken");
      await authHttp.post(
        "/auth/api/change-password",
        { userId: user.id, oldPassword, newPassword },
        {
          headers: {
            ...(token ? { "X-Client-Token": token } : {}),
            ...(refresh ? { "X-Client-Refresh-Token": refresh } : {})
          }
        }
      );
      setOldPassword("");
      setNewPassword("");
      setConfirm("");
      setMessage("Password updated.");
    } catch (err: unknown) {
      const response = (err as { response?: { data?: { message?: string } } }).response;
      setError(response?.data?.message || "Password was not changed.");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="space-y-6 pb-24">
      <div>
        <div className="text-2xl font-bold">{title}</div>
        <div className="text-sm text-slate-500">{description}</div>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Account</CardTitle>
        </CardHeader>
        <CardContent className="space-y-1 text-sm">
          <div className="font-semibold">{user?.displayName || "Signed in"}</div>
          <div className="text-slate-500">{user?.email}</div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Change password</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <Input
            type="password"
            autoComplete="current-password"
            placeholder="Current password"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
          />
          <Input
            type="password"
            autoComplete="new-password"
            placeholder="New password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
          />
          <Input
            type="password"
            autoComplete="new-password"
            placeholder="Confirm new password"
            value={confirm}
            onChange={(e) => setConfirm(e.target.value)}
          />
          {error && <div className="text-sm text-rose-600">{error}</div>}
          {message && <div className="text-sm text-emerald-700">{message}</div>}
          <Button onClick={changePassword} isLoading={busy} disabled={busy}>
            Update password
          </Button>
        </CardContent>
      </Card>

      {children}

      <Card>
        <CardHeader>
          <CardTitle>Delete account</CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          <div className="text-sm text-slate-500">
            Type DELETE to confirm. Account deletion is not available on the platform yet, so this does not send a request.
          </div>
          <Input
            value={deleteText}
            placeholder="DELETE"
            onChange={(e) => setDeleteText(e.target.value)}
          />
          {deleteNote && <div className="text-sm text-slate-600">{deleteNote}</div>}
          <Button
            variant="danger"
            disabled={deleteText !== "DELETE"}
            onClick={() => setDeleteNote("Account deletion is not available.")}
          >
            Delete account
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
